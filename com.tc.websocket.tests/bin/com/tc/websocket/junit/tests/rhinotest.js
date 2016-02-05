//init code block start

java.lang.System.out.println("event is " + event);
if("onOpen".equals(event)){
	onOpen();
}else if("onMessage".equals(event)){
	onMessage();
}else if("onClose".equals(event)){
	onClose();
}else if("onError".equals(event)){
	onError();
}


function onOpen(){
	java.lang.System.out.println("onOpen called");
}

function onMessage(){
	java.lang.System.out.println("onMessage called");
	java.lang.System.out.println(session.getEffectiveUserName());
	java.lang.System.out.println(session.getPlatform());
}

function onClose(){
	java.lang.System.out.println("onClose called");
}
