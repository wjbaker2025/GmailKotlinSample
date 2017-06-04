package org.hevin.gmailkotlion.adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.SparseBooleanArray
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.hevin.gmailkotlion.R
import org.hevin.gmailkotlion.helper.CircleTransform
import org.hevin.gmailkotlion.helper.FlipAnimator
import org.hevin.gmailkotlion.model.Message

class MessageAdapter(private val context: Context, private val messages: ArrayList<Message>,
                     private val listener: MessageAdapterListener)
    : RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    val selectedItems: SparseBooleanArray = SparseBooleanArray()
    val animationItemsIndex: SparseBooleanArray = SparseBooleanArray()

    var reverseAllAnimations: Boolean = false
    var currentSelectedIndex: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_main_message, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val msg = messages[position]

        holder.from.text = msg.from
        holder.subject.text = msg.subject
        holder.message.text = msg.message
        holder.timestamp.text = msg.timestamp

        holder.iconText.text = msg.from.substring(0, 1)
        holder.itemView.isActivated = selectedItems.get(position, false)

        applyReadStatus(holder, msg)
        applyImportant(holder, msg)
        applyProfilePicture(holder, msg)
        applyIconAnimation(holder, position)
        applyClickEvents(holder, position)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    private fun applyClickEvents(holder: MyViewHolder, position: Int) {
        holder.iconContainer.setOnClickListener {
            listener.onIconClicked(position)
        }

        holder.iconImp.setOnClickListener {
            listener.onIconImportantClicked(position)
        }

        holder.messageContainer.setOnClickListener {
            listener.onMessageRowClicked(position)
        }

        holder.messageContainer.setOnLongClickListener { view ->
            listener.onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        }
    }

    private fun applyProfilePicture(holder: MyViewHolder, message: Message) {
        if (!TextUtils.isEmpty(message.picture)) {
            Glide.with(context).load(message.picture)
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(CircleTransform(context))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgProfile)
        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle)
            holder.imgProfile.setColorFilter(message.color)
            holder.iconText.visibility = View.VISIBLE
        }
    }

    private fun applyIconAnimation(holder: MyViewHolder, position: Int) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.visibility = View.GONE
            resetIconYAxis(holder.iconBack)
            holder.iconBack.visibility = View.VISIBLE
            holder.iconBack.alpha = 1f
            if (currentSelectedIndex == position) {
                FlipAnimator(context).flipView(holder.iconBack, holder.iconFront, true)
                resetCurrentIndex()
            }
        } else {
            holder.iconBack.visibility = View.GONE
            resetIconYAxis(holder.iconFront)
            holder.iconFront.visibility = View.VISIBLE
            holder.iconFront.alpha = 1f
        }
    }

    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    fun resetAnimationIndex() {
        reverseAllAnimations = false
        animationItemsIndex.clear()
    }

    private fun applyImportant(holder: MyViewHolder, msg: Message) {
        if (msg.isImport) {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_black_24dp))
            holder.iconImp.setColorFilter(ContextCompat.getColor(context, R.color.icon_tint_selected))
        } else {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_border_black_24dp))
            holder.iconImp.setColorFilter(ContextCompat.getColor(context, R.color.icon_tint_normal))
        }
    }

    private fun applyReadStatus(holder: MyViewHolder, msg: Message) {
        if (msg.isRead) {
            holder.from.setTypeface(null, Typeface.NORMAL)
            holder.subject.setTypeface(null, Typeface.NORMAL)
            holder.from.setTextColor(ContextCompat.getColor(context, R.color.subject))
            holder.subject.setTextColor(ContextCompat.getColor(context, R.color.message))
        } else {
            holder.from.setTypeface(null, Typeface.BOLD)
            holder.subject.setTypeface(null, Typeface.BOLD)
            holder.from.setTextColor(ContextCompat.getColor(context, R.color.from))
            holder.subject.setTextColor(ContextCompat.getColor(context, R.color.subject))
        }
    }

    fun toggleSelection(position: Int) {
        currentSelectedIndex = position
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position)
            animationItemsIndex.delete(position)
        } else {
            selectedItems.put(position, true)
            animationItemsIndex.put(position, true)
        }
        notifyItemChanged(position)
    }

    fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<Int> {
        val list = ArrayList<Int>(selectedItems.size())
        (0 until selectedItems.size()).mapTo(list) { selectedItems.keyAt(it) }
        return list
    }

    fun removeData(position: Int) {
        messages.removeAt(position)
        resetCurrentIndex()
    }

    fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    inner class MyViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

        val from: TextView = itemView.findViewById(R.id.tv_from) as TextView
        val subject: TextView = itemView.findViewById(R.id.tv_primary) as TextView
        val message: TextView = itemView.findViewById(R.id.txt_secondary) as TextView
        val iconText: TextView = itemView.findViewById(R.id.icon_text) as TextView
        val timestamp: TextView = itemView.findViewById(R.id.timestamp) as TextView

        val iconBack: RelativeLayout = itemView.findViewById(R.id.icon_back) as RelativeLayout
        val iconFront: RelativeLayout = itemView.findViewById(R.id.icon_front) as RelativeLayout
        val iconImp: ImageView = itemView.findViewById(R.id.icon_star) as ImageView
        val imgProfile: ImageView = itemView.findViewById(R.id.icon_profile) as ImageView

        val messageContainer: LinearLayout = itemView.findViewById(R.id.message_container) as LinearLayout
        val iconContainer: RelativeLayout = itemView.findViewById(R.id.icon_container) as RelativeLayout

        init {
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(view: View): Boolean {
            listener.onRowLongClicked(adapterPosition)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return true
        }
    }

    interface MessageAdapterListener {
        fun onIconClicked(position: Int)
        fun onIconImportantClicked(position: Int)
        fun onMessageRowClicked(position: Int)
        fun onRowLongClicked(position: Int)
    }
}