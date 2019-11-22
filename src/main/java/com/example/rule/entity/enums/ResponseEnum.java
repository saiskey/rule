package com.example.rule.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ResponseEnum {

    //成功
    SUCCESS(10000, "成功"),

    //常用异常code 值 10001~11000
    METHOD_ARGUMENT_NOT_VALID_ERROR(10001, "请求参数校验不合格"),
    PLEASE_CHOOSE_OPERATION_OBJECT(10002, "请选择操作对象"),
    SERVICE_MATURITY(10003, "您好,您的租户服务已到期"),
    ACCOUNT_FORBIDDEN(10004, "账户被禁用"),
    TOKEN_NOT_FOUND(4011, "未找到请求凭证，请登录后重试"),
    NO_COOKIE(4012, "无cookie"),
    UNAUHORIZATION(4013, "权限不足"),
    LOGIN_TOKEN_ACCESS_TIME_OUT_PLEASE_TRY_RELOGIN(4014, "访问令牌已过期，请登录后重试"),
    RESPONSE_STATUS_EXCEPTION(10005, "服务器响应异常"),
    LIST_CHANGE_TREE_CANT_FIND_ROOT_NODE(10007, "列表转换成树时未找到根节点"),
    DID_NOT_FOUND_LABEL_CONTENT_TYPE_ENUM_BY_THIS_CODE(10008, "未根据评价标签code找到对应评价枚举对象"),
    UNKNOW_EVALUATE(10009, "未知的评价维度"),
    REQUEST_METHOD_UNSUPPORT_ERROR(10010, "不支持的请求方式"),
    API_ACCOUNT_TYPE_NOT_SUPPORT(10011, "不支持的用户类型"),
    HTTP_MESSAGE_NOT_READABLE(10013, "请求内容不可读"),
    PLEASE_CHOOSE_RIGHT_TARGET_TO_OPERATION(10014, "请选择正确的操作目标"),
    UNKNOW_MESSAGE_SEND_WAY(10015, "未知的发送消息方式"),
    UNKNOW_NOTIFY_TYPE(10016, "未知的通知类型"),
    THIS_SHORT_MESSAGE_NOT_MATCH_ANY_TEMPLATE(10017, "该通知没有任何已申请的短信模板，发送失败"),
    WEB_MESSAGE_DONT_HAVE_ANY_RECEIVER(10018, "该站内信没有任何通知对象"),
    EXPORT_ERROR(10019, "导出字段不能少于2个，请重新选择！"),
    EXPORT_EXCEL_FAILED(10020, "生成excel失败"),
    PARAM_IS_NULL(10021,"请求参数不能为空"),

    //用户模块code 使用 11001~19999
    PHONE_EMAIL_EXISt(11099,"手机号或邮箱已经存在"),
    USERNAME_EXIST(11001, "用户名已存在"),
    EMAIL_EXIST(11002, "邮箱已存在"),
    ORGANIZE_CODE_EXIST(11020, "维护单元编码重复，请重新输入"),
    PHONE_EXIST(11022, "手机号已存在"),
    NAME_EXIST(11033,"企业名已存在"),
    ACCOUNT_NOT_EXIST(11003, "账户不存在,请确认账号密码"),
    ACCOUNT_NAME_NOT_EXIST(11103,"账户名输入错误,请确认账户名"),
    ACCOUNT_PASSWORD_NOT_EXIST(11104,"密码输入错误,请确认密码"),
    VERIFY_CODE_DEFEAT(11004, "您的验证码有误,请重新输入"),
    SMS_CODE_DEFEAT(11005, "短信验证码发送失败"),
    EMAIL_CODE_DEFEAT(11006, "邮箱验证码发送失败"),
    RESET_PASSWORD_DEFEAT(11007, "重置密码失败"),
    TENANT_INSERT_DEFEAT(11008, "租户新增失败"),
    JWT_INVALID_CLAIM(11010, "当前登录信息失效，请重新登录"),
    TENANT_ACCOUNT_NOT_FOUND(11011, "未找到该租户员工"),
    PLEASE_CHOOSE_TENANT_GROUP(11012, "请选择员工组"),
    TENANT_ACCOUNT_IS_NOT_HAVE_ANY_GROUP(11013, "该员工未在任何组内"),
    CAN_NOT_READD_IN_GROUP(11014, "以下员工已在该组内，请勿重复添加"),
    CAN_NOT_OPERATION_ADMIN(11015, "超级管理员为系统创建，不能进行编辑、删除、启用禁用与重置密码操作"),
    ACCOUNT_NAME_CAN_NOT_CONTAIN_SPECIAL_SYMBOLS(11016, "用户姓名不准包含特殊字符"),
    VERIFICATION_CODE_INPUT_ERROR(11017, "验证码不匹配,请确认验证码"),
    WRONG_PASSWORD(11018, "旧密码密码输入错误,若忘记密码请点击往忘记密码操作"),
    CELL_NAME_REPEAT(11019, "维护单元名称不能重复"),
    MAINTAIN_RELATION_NOT_EXIST(11020, "运维关系不存在"),
    CELL_NOT_EXIST(11021, "维护单元为空"),
    OCR_FAIL(11022, "OCR认证失败"),

    //客户模块
    CUSTOMER_NOT_EXIST(15000, "客户信息不存在"),
    CUSTOMER_USERNAME_EXIST(15003, "账户名不能重复"),

    CUSTOMER_CELL_NOT_EXIST(15001,"客户门店信息不存在"),
    //客户设置
    ADD_CUSTOMER_FIELD_FAIL(12000, "添加客户自定义字段失败"),
    ADD_CUSTOMER_FIELD_VALUE_FAIL(12001, "添加客户自定义字段值失败"),
    CREATE_CUSTOMER_TEMPLATE_FAIL(12002, "添加客户模版失败"),
    UPDATE_CUSTOMER_TEMPLATE_FAIL(12003, "修改客户模版失败"),
    UPDATE_CUSTOMER_FIELD_FAIL(12004, "修改客户自定义字段失败"),
    ADD_CUSTOMER_CELL_TEMPLATE_FAIL(12005, "添加维护单元模版失败"),
    UPDATE_CUSTOMER_CELL_TEMPLATE_FAIL(12005, "修改维护单元模版失败"),
    SET_MAINTAIN_FAIL(12006, "添加客户运维关系失败"),
    FIND_MAINTAIN_FAIL(12007, "查找客户运维关系失败"),
    ADD_CUSTOMER_CELL_EMPLOYEE_FAIL(12008, "添加客户详情联系人失败"),
    CUSTOMER_FIELD_IS_INUSE(12009, "该字段已在模版中使用"),
    DELETE_FIELD_FAIL(12010, "删除自定义字段失败"),
    ENABLE_FIELD_FAIL(12011, "启用自定义字段失败"),
    CUSTOMER_GROUP_SORT_FAIL(12012, "客户组织排序失败"),
    ADD_CUSTOMER_FIELD_NAME_REPEAT(12013, "客户字段名称重复"),
    CUSTOMER_TEMPLATE_NAME_REPEAT(12014, "客户模板名称重复"),
    CUS_CELL_TEMPLATE_NAME_REPEAT(12015, "维护单元模板名称重复"),

    //客户代表code 11111-11199
    REPRESENT_USERNAME_EXIST(11111, "客户代表账户已存在,请重新输入"),
    REPRESENT_CUSTOMER_EXIST(11112, "存在已绑定客户，绑定失败"),
    REPRESENT_NAME_EXIST(11113, "客户代表名称已存在,请重新输入"),
    REPRESENT_NAME_SPECIAL_CHAR(11114, "客户代表名不能包含特殊字符,请重新输入"),


    //租户-员工管理 code 11200-11300
    GROUP_HAVE_ACCOUNTS_DEL_FAIL(11200, "删除失败，该员工组下有员工未移除"),
    GROUP_HAVE_SON_GROUP_DEL_FAIL(11201, "删除失败，该员工组下有其他员工组"),
    GET_GROUPS_FAIL(11202, "获取员工组失败"),
    ACCOUNT_HAVE_ORDER_IN_HAND_UPDATE_FAIL(11203, "修改员工信息失败，该员工当前有工单正在处理！"),
    IS_ROOT_GROUP_DEL_FAIL(11204, "删除失败，该员工组为根组，禁止删除"),
    ROLE_IS_USING(11205, "当前角色正在使用，无法删除"),
    GROUP_BIND_TRIGGER_OR_WORKFLOW(11206, "操作失败，该员工组有绑定触发器或者工作流"),
    SORT_GROUP_MUST_BELONG_SAME_PARENT(11207, "排序员工组必须属于同一父组下"),
    GROUP_HAVE_ORDER_IN_HANDLER(11208, "操作失败，该员工组有正在处理的工单"),

    //系统参数错误  10200~10300
    ID_NOT_EXISTS(10200, "参数ID不能为空！"),
    ENTITY_NOT_EXISTS(10201, "数据不存在！"),

    //需求中心code  18000~19999
    REQUIREMENT_NOT_EXISTS(18000, "该工单不是由需求中心创建,需求信息不存在"),
    //该需求已被受理
    REQUIREMENT_ACCEPTED(18001, "该需求已被受理"),

    //工单模块code 使用 20000～29999
    KNOWLEDGE_TYPE_MOVE_FAIL(20099, "请选择要移动到的知识分类"),
    KNOWLEDGE_TYPE_IS_NOT_EXIST(20100, "知识库类型不存在"),
    KNOWLEDGE_TYPE_HAS_CHILD_TYPE(20101, "无法删除，当前知识分类含有子分类！"),
    KNOWLEDGE_TYPE_HAS_POINT(20102, "无法删除，当前组别下还有知识点！"),
    CREATE_ORDER_FAIL(20103, "创建工单失败"),
    ORDER_TEMPLATE_ALREADY_BINDING(20104, "添加失败，客户在该工单类型下已绑定有工单模板，请重新选择"),
    ORDER_TYPE_IS_NOT_EXIST(20105, "工单类型不存在"),
    CREATE_ORDER_TEMPLATE_FAIL(20106, "创建工单模版失败"),
    ORDER_TEMPLATE_IS_USING(20107, "工单模版正在使用中，无法修改"),
    CUSTOMER_IS_HAS_TEMPLATE(20108, "该客户已拥有该类型的模版，修改失败"),
    UPDATE_ORDER_TEMPLATE_FAIL(20109, "修改工单模版失败"),
    DELETE_ORDER_TEMPLATE_FAIL(20110, "删除工单模版失败"),
    ORDER_IS_USING_TEMPLATE(20219, "停用失败，该模板当前有工单正在进行，请稍后再试"),
    REQUIREMENT_NOT_EXIST(20111, "需求不存在"),
    TEMPLATE_NOT_EXIST(20112, "模版不存在"),
    FIELD_NOT_EXIST(20113, "自定义字段不存在"),
    CREATE_FIELD_FAIL(20114, "插入订单自定义字段失败"),
    ADD_KNOWLEDGE_TYPE_FAIL(20115, "添加知识类型失败"),
    UPDATE_ORDER_FAIL(20116, "修改工单失败"),
    SLA_NOT_EXIST(20117, "sla不存在"),
    USED_BY_ORDER_TEMPLATE(20118, "工作流已经被工单模板使用！"),
    KNOWLEDGE_TYPE_IS_NOT_SUIT(20119, "请选择最小节点工单类型"),
    KNOWLEDGE_IS_TOP(20120, "已是最高级无法上移"),
    KNOWLEDGE_IS_BOTTOM(20121, "已是最低级无法下移"),
    TEMPLATE_IS_INUSE(20122, "无法删除，模版正在使用中"),
    CUSTOMER_TEMPLATE_IS_EXIST(20123, "该客户已有维护单元模板，请重新选择关联客户"),
    ORDER_FIELD_EXIST(20124, "工单模板存在引用此自定义字段，不能删除"),
    CUSTOMER_IS_HAS_TEMPLATE_ADD_FAIL(20125, "该客户已拥有该类型的模版，新增失败"),
    DID_NOT_FOUND_THIS_ORDER_OLA_RECORD(20126, "未找到该工单的ola处理记录，该工单处理存在异常"),
    UNKNOW_ORDER_REQUIREMENT_TYPE(20127, "未知需求类型"),
    GROUP_NAME_REPEAT(20128, "员工组织名称重复"),
    CUSTOMER_IS_HAS_AVA_TEMPLATE(20109, "该客户在该工单类型下已有启用的模板，请先停用原有模板"),
    TEMPLATE_IS_USED_CANOT_DISABLE(20110, "模版正在使用中，无法禁用"),


    ORDER_NOT_HAVE_OTHER_HANDLER(20200, "工单不存在其他处理人"),
    ORDER_HANDLER_GROUP_IS_NOT_CURRENT(20201, "当前操作组不是工单指定的处理组"),
    ORDER_HANDLER_IS_NOT_CURRENT(20202, "当前操作人不是工单指定的处理人"),

    ORDER_CUSTOM_FIELD_NAME_REPEAT(20203, "工单字段名称重复"),
    ORDER_TEMPLATE_FIELD_NAME_REPEAT(20204, "工单模板名称重复"),

    ORDER_CHANGE_FAIL(20205,"转单失败"),


    ORDER_TYPE_LEVEL_OUT(21000, "工单类型等级超出系统默认"),
    ORDER_TYPE_HAS_CHILD(21001, "该分类下存在子分类，无法删除！"),
    ORDER_TYPE_USING(21002, "该分类绑定有工单模板或已生成工单，无法删除！"),
    ORDER_TYPE_IS_DEFAULT(21003, "该工单类型为顶级分类，无法进行当前操作"),
    ORDER_TYPE_IS_ENABLE(21004, "该工单类型已经启用/停用，请勿重复操作"),
    ORDER_TYPE_INSERT_ERROR(21005, "工单类型插入失败"),
    ORDER_TYPE_DELETE_ERROR(21006, "工单类型删除失败"),
    ORDER_TYPE_UPDATE_ERROR(21007, "工单类型编辑失败"),
    ORDER_TYPE_GET_ERROR(21008, "工单类型获取失败"),
    ORDER_TYPE_UPDATE_ENABLE_ERROR(21009, "工单类型状态修改失败"),

    //数据字典部分
    DEL_ERROR(21010,"删除失败,系统异常！"),
    EXIST_SUN(21011,"当前分类下存在服务技能，不允许删除！"),
    DEL_FAIL(21012,"删除失败！"),
    INSER_FAIL(21013,"新增失败！"),
    UPDATE_FAIL(21014,"修改失败！"),
    GET_DICTION_TREE_FAIL(21015,"获取数据字典tree失败！"),

    ORDER_TYPE_SORT_FAIL(90101, "服务目录排序失败"),
    ORDER_TYPE_UP_FAIL(90102, "当前节点已无法再上移"),
    ORDER_TYPE_DOWN_FAIL(90103, "当前节点已无法再下移"),

    //配置 code 使用 90000~99998
    UPDATE_KNOWLEDGE_FAIL(90001, "修改知识点失败"),
    DELETE_KNOWLEDGE_TYPE_FAIL(90002, "删除知识类型失败"),
    DELETE_KNOWLEDGE_FAIL(90003, "删除知识点失败"),
    ADD_KNOWLEDGE_FAIL(90004, "录入知识点失败"),
    ADOPTED_KNOWLEDGE(9005, "该工单已采用此知识点"),


    //服务管理 90100-90199
    SERVER_CATALOG_NO_CHOOSE(90100, "请选择一个服务目录"),
    SERVER_CATALOG_SORT_FAIL(90101, "服务目录排序失败"),
    DELETE_SERVER_LEVEL_FAIL(90102, "删除服务级别失败"),
    DELETE_SERVER_CATALOG_FAIL(90103, "删除服务目录失败"),
    INSERT_SERVER_CATALOG_FAIL(90105, "新增服务目录失败"),
    SERVER_CATALOG_GET_FAIL(90104, "获取服务目录失败"),
    SERVER_CATALOG_NOT_EXIST(90106, "服务目录不存在"),
    SERVER_CATALOG_IN_USE(90107, "服务目录使用中"),
    SERVER_LEVEL_IN_USE(90108, "服务级别使用中"),

    //系统级别异常
    DATA_ADD_FAILED(99001, "服务调用失败！"),
    SERVICE_CALL_FAILED(99001, "服务调用失败！"),
    ERROR(99999, "系统异常，请稍后再试");

    @Getter
    private final int code;
    @Getter
    private final String desc;

}
