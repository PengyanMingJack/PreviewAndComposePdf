package com.zchd.pdf

data class SignPdfInfo(
    var userName: String = "",//授权人姓名
    var userCardType: String = "",//授权人证件类型
    var userIdCard: String? = "",//授权人身份证号
    var houseRelation: String? = null,//授权人关系
    var signPicPath: String? = null,//授权人签字图片
    var fingerPicPath: String? = null,//授权人指纹图片
    var repName: String? = "",//代理人姓名
    var repCardType: String = "",//代理人证件类型
    var repIdCard: String? = "",//代理人身份证号
    var authRelation: String? = null,//代理人关系
    var repSignPicPath: String? = null,//代理人签字图片路径
    var repFingerPicPath: String? = null,//代理人指纹图片路径
) : java.io.Serializable