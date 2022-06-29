package com.vincent.givetake.utils

object Constant {
    const val KEY_ACCESS_USER = "user"
    const val KEY_USER_ID = "user_id"
    const val PROFILE_DATA = "profile_data"
    const val PICK_IMAGE = 201
    const val KEY_ACCESS_EDIT = "edit"
    const val EDIT_ITEM_ID = "edit_item_id"
    const val EDIT_ITEM_ADDRESS = "edit_item_address"
    const val REQUEST_ACCESS = "request_access"
    const val REQUEST_ITEM_ID = "request_item_id"
    const val REQUEST_TOKEN_NOTIF = "request_token_notif"
    const val LIST_RECEIVER_ACCESS = "list_receiver_access"
    const val LIST_RECEIVER_ITEM_ID = "list_receiver_item_id"
    const val CHOOSE_RECEIVER_ACCESS = "choose_receiver_access"
    const val CHOOSE_RECEIVER_ITEM_ID = "choose_receiver_item_id"
    const val CHOOSE_RECEIVER_REASON = "choose_receiver_reason"
    const val CHOOSE_RECEIVER_REQUEST_ID = "choose_receiver_request_id"
    const val CHOOSE_RECEIVER_NAME = "choose_receiver_name"
    const val IS_CHOOSE_RECEIVER = "is_choose_receiver"
    const val IS_REDEEM_REWARD = "is_redeem_reward"
    const val RECEIVE_TOKEN = "receive_token"
    const val RECEIVE_ITEM_ID = "receive_item_id"
    const val MAP_DIRECTION_DATA = "map_direction_data"
    const val MY_TOKEN = "my_token"
    const val REWARD_ID = "reward_id"
    const val REWARD_DETAIL = "reward_detail"
    const val USER_POINT = "user_point"

    const val CHAT_ITEM = "item_chat"
    const val KEY_COLLECTION_CHAT = "chat"
    const val KEY_SENDER_ID = "senderId"
    const val KEY_RECEIVER_ID = "receiverId"
    const val KEY_ITEM_ID = "itemId"
    const val KEY_MESSAGE = "message"
    const val KEY_TIME_SEND = "timesend"
    const val KEY_CHAT_ID = "chatId"
    const val KEY_ITEM_NAME = "name"
    const val KEY_COLLECTION_CONVERSATIONS = "conversations"
    const val KEY_CONVERSION = "conversion"

    const val KEY_SENDER_USERNAME = "senderName"
    const val KEY_RECEIVER_USERNAME = "receiverName"
    const val KEY_LAST_MESSAGE = "lastMessage"

    const val KEY_OTP = "key_otp"
    const val KEY_PHONE = "key_phone"
    const val KEY_USERNAME = "key_username"
    const val KEY_PASSWORD = "key_password"

    const val FCM_TYPE = "fcmType"
    const val FCM_TYPE_CHAT = "fcmTypeChat"
    const val FCM_TYPE_ITEM = "fcmTypeItem"
    const val FCM_ITEM_NAME = "fcmItemName"
    const val FCM_CHAT_NAME = "fcmChatName"
    const val FCM_TOKEN = "fcmToken"
    private const val REMOTE_MSG_AUTHORIZATION = "Authorization"
    private const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
    const val REMOTE_MSG_DATA = "data"
    const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"

    var remoteMsgHeaders : HashMap<String, String>? = null
    fun getterRemoteMsgHeaders() : HashMap<String, String> {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = HashMap()
            remoteMsgHeaders!![REMOTE_MSG_AUTHORIZATION] = "key=AAAA7PUszrE:APA91bGM9Vyq0CyNKNsIxndcskxnuTiFlqQo4ZuUC-xNMXdqqJ_UpwEjzvglu5VYH0eaWDccd-5C9GRq5zf1popsAGmMn5OHhMDBA4q_mQIMab8tsMxZiaf515rAd6ydoUbgvZftZ_1d"
            remoteMsgHeaders!![REMOTE_MSG_CONTENT_TYPE] = "application/json"
        }
        return remoteMsgHeaders!!
    }

    const val ADVICE_DETAIL_TYPE = "advice_detail_type"
    const val ADVICE_DETAIL_TITLE = "advice_detail_title"
    const val ADVICE_DETAIL_DESC = "advice_detail_desc"
    const val ADVICE_DETAIL_REPLY = "advice_detail_reply"

    const val UPDATE_PASS_KEY = "update_pass_key"
    const val UPDATE_PASS_USERNAME = "update_pass_username"

}