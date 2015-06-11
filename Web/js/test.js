function send(message){
    if(sock.readyState == SockJS.OPEN){
        console.log("Sending");
        sock.send(message);
    } else{
        console.log("Sock is not open");
    }
}