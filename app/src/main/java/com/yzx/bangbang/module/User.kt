package com.yzx.bangbang.module

import java.io.Serializable


//数据库中为account表
 class User internal constructor(var name: String, var account: String, var bbContact: String, var region: String, var signature: String, var sex: Int,var id: Int) : Serializable


