package org.hevin.gmailkotlion.helper

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import org.hevin.gmailkotlion.R

class FlipAnimator(context: Context) {
    var leftIn: AnimatorSet = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in) as AnimatorSet
    var leftOut: AnimatorSet = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out) as AnimatorSet
    var rightIn: AnimatorSet = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in) as AnimatorSet
    var rightOut: AnimatorSet = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out) as AnimatorSet

    fun flipView(back: View, front: View, showFront: Boolean) {
        val showFrontAnim = AnimatorSet()
        val showBackAnim = AnimatorSet()

        leftIn.setTarget(back)
        rightOut.setTarget(front)
        showFrontAnim.playTogether(leftIn, rightOut)

        leftOut.setTarget(back)
        rightIn.setTarget(front)
        showBackAnim.playTogether(rightIn, leftOut)

        if (showFront) {
            showFrontAnim.start()
        } else {
            showBackAnim.start()
        }
    }
}