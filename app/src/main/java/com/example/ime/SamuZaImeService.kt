package com.example.ime

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.clipboard.ClipboardHistoryManager
import com.example.privacy.SecretMessageCipher
import com.example.ui.keyboard.KeyboardActionListener
import com.example.ui.keyboard.SamuZaKeyboardView

class SamuZaImeService : InputMethodService() {

    private val serviceLifecycleOwner = ServiceLifecycleOwner()
    private var clipboardListener: ClipboardManager.OnPrimaryClipChangedListener? = null

    override fun onCreate() {
        super.onCreate()
        serviceLifecycleOwner.onCreate()
        ClipboardHistoryManager.init(this)

        // Clipboard listener to automatically record and auto-decode copied secret messages
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboardListener = ClipboardManager.OnPrimaryClipChangedListener {
            val clip = clipboard?.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val copiedText = clip.getItemAt(0).text?.toString() ?: ""
                if (copiedText.isNotEmpty()) {
                    ClipboardHistoryManager.addItem(this, copiedText)
                }
            }
        }
        clipboardListener?.let { clipboard?.addPrimaryClipChangedListener(it) }
    }

    override fun onCreateInputView(): View {
        serviceLifecycleOwner.onStart()

        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setViewTreeLifecycleOwner(serviceLifecycleOwner)
            setViewTreeSavedStateRegistryOwner(serviceLifecycleOwner)
            setViewTreeViewModelStoreOwner(serviceLifecycleOwner)

            setContent {
                SamuZaKeyboardView(
                    listener = object : KeyboardActionListener {
                        override fun onCommitText(text: String) {
                            currentInputConnection?.commitText(text, 1)
                        }

                        override fun onSetComposingText(text: String) {
                            currentInputConnection?.setComposingText(text, 1)
                        }

                        override fun onFinishComposingText() {
                            currentInputConnection?.finishComposingText()
                        }

                        override fun onDelete() {
                            val conn = currentInputConnection ?: return
                            val selected = conn.getSelectedText(0)
                            if (selected != null && selected.isNotEmpty()) {
                                conn.commitText("", 1)
                            } else {
                                conn.deleteSurroundingText(1, 0)
                            }
                        }

                        override fun onAction() {
                            val conn = currentInputConnection ?: return
                            val editorInfo = currentInputEditorInfo
                            val imeOptions = editorInfo?.imeOptions ?: EditorInfo.IME_ACTION_DONE
                            val action = imeOptions and EditorInfo.IME_MASK_ACTION

                            if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
                                conn.performEditorAction(action)
                            } else {
                                sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
                            }
                        }

                        override fun onSwitchIme() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                switchToPreviousInputMethod()
                            } else {
                                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.showInputMethodPicker()
                            }
                        }

                        override fun onHide() {
                            requestHideSelf(0)
                        }
                    }
                )
            }
        }

        return composeView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        serviceLifecycleOwner.onStart()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        serviceLifecycleOwner.onStop()
    }

    override fun onDestroy() {
        clipboardListener?.let { listener ->
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboard?.removePrimaryClipChangedListener(listener)
        }
        serviceLifecycleOwner.onDestroy()
        super.onDestroy()
    }
}
