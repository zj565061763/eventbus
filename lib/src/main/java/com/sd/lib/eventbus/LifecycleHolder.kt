package com.sd.lib.eventbus

import android.app.Activity
import android.app.Dialog
import android.view.View
import java.lang.ref.WeakReference

internal abstract class LifecycleHolder {
    private var _viewRef: WeakReference<View>? = null

    private val currentView: View?
        get() {
            val view = _viewRef?.get()
            if (view == null) {
                _enable = false
            }
            return view
        }

    private var _enable = false
        set(value) {
            if (field != value) {
                field = value
                onLifecycleChanged(value)
            }
        }

    fun setActivity(activity: Activity) {
        if (activity.isFinishing) {
            setView(null)
            return
        }

        val window = requireNotNull(activity.window) { "Activity.getWindow() is null." }
        setView(window.decorView)
    }

    fun setDialog(dialog: Dialog) {
        val context = dialog.context
        if (context is Activity && context.isFinishing) {
            setView(null)
            return
        }

        val window = requireNotNull(dialog.window) { "Dialog.getWindow() is null." }
        setView(window.decorView)
    }

    fun setView(view: View?) {
        val oldView = currentView
        if (oldView == view) return

        oldView?.removeOnAttachStateChangeListener(_onAttachStateChangeListener)
        _viewRef = if (view == null) null else WeakReference(view)
        view?.addOnAttachStateChangeListener(_onAttachStateChangeListener)

        checkEnableState()
    }

    private fun checkEnableState() {
        val context = currentView?.context
        if (context is Activity && context.isFinishing) {
            _enable = false
            return
        }
        _enable = currentView?.isAttachedToWindow ?: false
    }

    private val _onAttachStateChangeListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            if (currentView === v) {
                _enable = true
            }
        }

        override fun onViewDetachedFromWindow(v: View) {
            if (currentView === v) {
                _enable = false
            }
        }
    }

    protected abstract fun onLifecycleChanged(enable: Boolean)
}