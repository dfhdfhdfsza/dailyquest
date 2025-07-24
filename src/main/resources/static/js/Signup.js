document.querySelector("#id-input").addEventListener("input",function(){    //사용자가 입력할때
    const id=this.value;    //입력된값 가져오기

    if(!id.trim()){ //id가 빈문자열이면 id.trim은 false
        document.getElementById("id-check-result").innerText="";    //중복확인메시지 초기화
        return;
    }

    fetch(`/api/users/check-id?id=${id}`)   //중복체크 요청
        .then(res=>res.json())
        .then(exists=>{
            if(exists){
                document.getElementById("id-check-result").innerText="이미 사용중인 아이디입니다.";
                document.getElementById("id-check-result").style.color="red";

            }else{
                document.getElementById("id-check-result").innerText="사용 가능한 아이디입니다.";
                document.getElementById("id-check-result").style.color="green";
            }
        });
})