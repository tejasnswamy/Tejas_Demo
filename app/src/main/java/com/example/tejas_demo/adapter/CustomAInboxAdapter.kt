package com.example.tejas_demo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.clevertap.android.sdk.inbox.CTInboxMessage
import com.example.tejas_demo.databinding.CustomAppInboxCardLayoutBinding

class CustomAInboxAdapter(var caiList: ArrayList<CTInboxMessage>, var context: Context) : RecyclerView.Adapter<CustomAIHolder>() {

    private var arrList: ArrayList<CTInboxMessage> = caiList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAIHolder {
        return CustomAIHolder(CustomAppInboxCardLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }


    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: CustomAIHolder, position: Int) {
        val list = arrList[position]
        holder.binding.customAiMessage.text = list.inboxMessageContents[0].message
        holder.binding.customAiTitle.text = list.inboxMessageContents[0].title


    }

    override fun getItemCount() = arrList.size
}

class CustomAIHolder(val binding: CustomAppInboxCardLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
}
