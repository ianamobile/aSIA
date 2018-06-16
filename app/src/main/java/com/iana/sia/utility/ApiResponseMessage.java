package com.iana.sia.utility;

import java.io.Serializable;


public class ApiResponseMessage extends GlobalVariables  implements Serializable {

	private int code;       // Response Code
	private String type;    // Response Type
	private String message; // SUCCESS Response Message
	private String details; // Additional details about the error
	private APIReqErrors apiReqErrors;
	
	public ApiResponseMessage(){}
	
	public ApiResponseMessage(int code, String message, APIReqErrors apiReqErrors){
		this.code = code;
		switch(code){
		case ERROR:
			setType("error");
			break;
		case WARNING:
			setType("warning");
			break;
		case INFO:
			setType("info");
			break;
		case OK:
			setType("ok");
			break;
		case TOO_BUSY:
			setType("too busy");
			break;
		default:
			setType("unknown");
			break;
		}
		this.message = message;
		this.apiReqErrors = apiReqErrors;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public APIReqErrors getApiReqErrors() {
		return apiReqErrors;
	}

	public void setApiReqErrors(APIReqErrors apiReqErrors) {
		this.apiReqErrors = apiReqErrors;
	}

	@Override
	public String toString() {
		return "ApiResponseMessage [code=" + code + ", type=" + type
				+ ", message=" + message + ", details=" + details
				+ ", apiReqErrors=" + apiReqErrors + "]";
	}
	
	
/*	public static void main(String[] args) {
		ApiResponseMessage a = new ApiResponseMessage();
		a.setCode(1);
		a.setType("error");
		a.setMessage("asdfasdf");
		APIReqErrors err= new APIReqErrors();
		List<Errors> l = new ArrayList<Errors>();
		Errors e = new Errors();
		e.setTransNum(10);
		e.setErrorCategory("Business");
		e.setErrorMessage("test");
		l.add(e);
		err.setErrors(l);
		a.setApiReqErrors(err);
		
		Gson g= new Gson();
		System.out.println(g.toJson(a));
		
		
	}*/
	
}
