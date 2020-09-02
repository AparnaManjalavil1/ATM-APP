package com.example.atm.ministatement

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.atm.R

class MiniStatementAdapter(
    var transactionList: ArrayList<MiniStatement>,
    var clickListener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val miniStatementView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_ministatement, parent, false)

        return MiniStatementViewHolder(miniStatementView)
    }

    override fun getItemCount(): Int {

        return transactionList.size


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MiniStatementViewHolder).initializeRowUIComponents(
            transactionList[position], clickListener

        )

    }

    inner class MiniStatementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewDate = itemView.findViewById<TextView>(R.id.transaction_date)
        var textViewTime = itemView.findViewById<TextView>(R.id.transaction_time)
        var textViewRemark = itemView.findViewById<TextView>(R.id.transaction_remark)
        var textViewAmount = itemView.findViewById<TextView>(R.id.transaction_amount)
        var textViewTransaction = itemView.findViewById<TextView>(R.id.transaction_remark_display)

        fun initializeRowUIComponents(mini: MiniStatement, action: OnItemClickListener) {
            textViewDate.text = mini.transactionDate
            textViewRemark.text = mini.remark
            textViewTime.text = mini.transactionTime
            if (textViewRemark.text == "debit") {
                textViewAmount.text = itemView.context.resources.getString(
                    R.string.mini_statement_amount,
                    mini.amount.toString()

                )
                textViewTransaction.text = itemView.context.getString(R.string.show_debit)
                textViewTransaction.setTextColor(Color.parseColor("#ff0000"))
            } else if (textViewRemark.text == "credit") {
                textViewAmount.text = itemView.context.resources.getString(
                    R.string.mini_statement_amount,
                    mini.amount.toString()
                )
                textViewTransaction.text = itemView.context.getString(R.string.show_credit)
                textViewTransaction.setTextColor(Color.parseColor("#ff669900"))
            } else if (textViewRemark.text == "transfer") {
                textViewAmount.text = itemView.context.resources.getString(
                    R.string.mini_statement_amount,
                    mini.amount.toString()
                )
                textViewTransaction.text = itemView.context.getString(R.string.show_debit)
                textViewTransaction.setTextColor(Color.parseColor("#ff0000"))
            }

            itemView.setOnClickListener {
                action.onItemClick(mini, adapterPosition)
            }

        }

    }

    interface OnItemClickListener {
        fun onItemClick(mini: MiniStatement, position: Int)
    }

}




