package com.ks3.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by qichao on 16/8/11.
 */
public class ExceptionConstants {

    public enum ErrorCode {

    	Redis_Error(HttpStatus.BAD_REQUEST,"redis unavailable"),
    	
        HttpRequestMethodNotSupported(HttpStatus.METHOD_NOT_ALLOWED, "HttpRequestMethodNotSupported"),

        BindException(HttpStatus.BAD_REQUEST,  "BindException"),

        NoSuchRequestHandlingMethodException(HttpStatus.METHOD_NOT_ALLOWED,  "NoSuchRequestHandlingMethodException"),

        HttpMediaTypeNotSupported(HttpStatus.UNSUPPORTED_MEDIA_TYPE,  "HttpMediaTypeNotSupported"),

        ApiNotFound(HttpStatus.NOT_FOUND, "ApiNotFound, Please check your http method and url."),

        ClientCallCancelled(HttpStatus.BAD_REQUEST,  "Client call cancelled."),

        ClientCallTimeout(HttpStatus.BAD_REQUEST,  "Client call time out."),

        HttpMessageNotReadable(HttpStatus.BAD_REQUEST,  "Request not readable."),

        InternalException(HttpStatus.INTERNAL_SERVER_ERROR, "We encountered an internal error, please try again later."),
        ThridSystemException(HttpStatus.INTERNAL_SERVER_ERROR, "调用第三方系统出错"),

        InvalidParameter(HttpStatus.BAD_REQUEST,"invalid parameters"),
        
        Topology_not_match(HttpStatus.BAD_REQUEST,"拓扑不一致，无法创建下发任务"),
        
        ConfigReason_not_match(HttpStatus.BAD_REQUEST,"配置变更原因不一致，无法下发任务"),
        
    	TaskStepNotExist(HttpStatus.BAD_REQUEST,"任务不存在"),
    	
    	DeviceInTaskStepNotExist(HttpStatus.BAD_REQUEST,"此设备未在此下发步骤中"),
    	
    	TransformBetweenJavaBeanAndJson(HttpStatus.BAD_REQUEST,"在JavaBean与Json之间转换错误"),
    	
    	DomainDeliverying(HttpStatus.BAD_REQUEST,"域名正在下发中"),
    	
    	GetNodeConfigFailure(HttpStatus.BAD_REQUEST,"未能成功获取域名在节点上的配置，请检查配置生成系统是否可用"),
    	
    	NextTaskStepWorking(HttpStatus.BAD_REQUEST,"下一步正在执行中，请确认是否有其他同事在操作"),
    	
    	TaskStepExecuteTimeOut(HttpStatus.BAD_REQUEST,"任务执行已超时，任务名称:{0} 任务步骤名称:{1} 任务ID:{2}"),
    	
    	GetDeviceFailure(HttpStatus.BAD_REQUEST,"获取设备列表失败"),
    	
    	GetTopologyInfoFailure(HttpStatus.BAD_REQUEST,"获取拓扑数据失败"),
    	
    	RetryTaskError(HttpStatus.BAD_REQUEST,"任务重试失败"),
    	
    	DeliveryUnavaiable(HttpStatus.BAD_REQUEST,"下发功能已禁用"),
    	
    	GetOriginDomainFailure(HttpStatus.BAD_REQUEST,"调用频道管理系统API，获取域名信息失败")
    	;
        private final HttpStatus status;

        private final String message;

        ErrorCode(HttpStatus status,  String message) {
            this.status = status;
            this.message = message;
        }

        public HttpStatus getStatus() {
            return this.status;
        }
        public String getMessage() {
            return this.message;
        }
    }
}
