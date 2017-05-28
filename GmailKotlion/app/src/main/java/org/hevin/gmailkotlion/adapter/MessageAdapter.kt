package org.hevin.gmailkotlion.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import org.hevin.gmailkotlion.R
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

        holder.iconText.text = msg.from!!.substring(0, 1)
        holder.itemView.isActivated = selectedItems.get(position, false)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    private fun applyClickEvents(holder: MyViewHolder, position: Int) {

    }

    private fun applyProfilePicture(holder: MyViewHolder, position: Int) {

    }

    private fun applyIconAnimation(holder: MyViewHolder, position: Int) {

    }

    private fun resetIconYAxis(view: View) {

    }

    private fun resetAnimationIndex() {

    }

    private fun applyImportant(holder: MyViewHolder, msg: Message) {

    }

    private fun applyReadStatus(holder: MyViewHolder, msg: Message) {

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

    fun getSelectedItemCount(): Int {
        return selectedItems.size()
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

        val iconBack: ImageView = itemView.findViewById(R.id.icon_back) as ImageView
        val iconFront: ImageView = itemView.findViewById(R.id.icon_front) as ImageView
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