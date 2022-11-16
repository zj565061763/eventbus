package com.sd.lib.eventbus

import android.app.Activity
import android.app.Dialog
import android.view.View
import java.lang.ref.WeakReference

internal abstract class LifecycleHolder {
    private var _viewRef: WeakReference<View>? = null

    private var _enable = false
        set(value) {
            if (field != value) {
                field = value
                onLifecycleChanged(value)
            }
        }

    private val currentView: View?
        get() = _viewRef?.get()

    fun setActivity(activity: Activity?) {
        if (activity == null) {
            setView(null)
            return
        }

        if (activity.isFinishing) {
            return
        }

        val window = requireNotNull(activity.window) { "Activity.getWindow() is null." }
        setView(window.decorView)
    }

    fun setDialog(dialog: Dialog?) {
        if (dialog == null) {
            setView(null)
            return
        }

        val context = dialog.context
        if (context is Activity && context.isFinishing) {
            return
        }

        val window = requireNotNull(dialog.window) { "Dialog.getWindow() is null." }
        setView(window.decorView)
    }

    fun setView(view: View?) {
        val oldView = currentView
        if (oldView != view) {

            val context = view?.context
            if (context is Activity && context.isFinishing) {
                return
            }

            oldView?.removeOnAttachStateChangeListener(_onAttachStateChangeListener)
            _viewRef = if (view == null) null else WeakReference(view)
            view?.addOnAttachStateChangeListener(_onAttachStateChangeListener)
            checkEnableState()
        }
    }

    private fun checkEnableState() {
        _enable = currentView?.isAttachedToWindow ?: false
    }

    private val _onAttachStateChangeListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            checkEnableState()
        }

        override fun onViewDetachedFromWindow(v: View) {
            checkEnableState()
        }
    }

    protected abstract fun onLifecycleChanged(enable: Boolean)
}