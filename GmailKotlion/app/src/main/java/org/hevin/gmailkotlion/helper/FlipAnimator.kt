package org.hevin.gmailkotlion.helper

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import org.hevin.gmailkotlion.R

class FlipAnimator {
    var leftIn: AnimatorSet? = null
    var leftOut: AnimatorSet? = null
    var rightIn: AnimatorSet? = null
    var rightOut: AnimatorSet? = null

    fun flipView(context: Context, back: View, front: View, showFront: Boolean) {
        leftIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in) as AnimatorSet?
        leftOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out) as AnimatorSet?
        rightIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in) as AnimatorSet?
        rightOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out) as AnimatorSet?

        val showFrontAnim = AnimatorSet()
        val showBackAnim = AnimatorSet()

        leftIn!!.setTarget(back)
        rightOut!!.setTarget(front)
        showFrontAnim.playTogether(leftIn, rightOut)

        leftOut!!.setTarget(back)
        rightIn!!.setTarget(front)
        showBackAnim.playTogether(rightIn, leftOut)

        if (showFront) {
            showFrontAnim.start()
        } else {
            showBackAnim.start()
        }
    }
}