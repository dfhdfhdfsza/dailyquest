document.addEventListener("DOMContentLoaded", function () { // 이벤트 등록
        //이메일 선택을 바꾸거나 직접입력창에 입력할때마다 이벤트 발생
        document.getElementById("email-id").addEventListener("input", updateFullEmail);
        document.getElementById("email-domain").addEventListener("change", updateFullEmail);
        document.getElementById("custom-domain").addEventListener("input", updateFullEmail);
    });

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

function handleDomainChange(){
    const select=document.getElementById("email-domain");
    const customInput=document.getElementById("custom-domain");

    if(select.value==="custom"){    //직접입력을 선택 했을 시 입력창 활성화
        customInput.style.display="inline";
        customInput.required=true;
    }else{                          //직접입력을 선택 안했을 시 입력창 비활성화
        customInput.style.display="none";
        customInput.required=false;
        customInput.value="";
    }
    updateFullEmail();
}

function updateFullEmail(){
    const emailId=document.getElementById("email-id").value;    //email 아이디부분
    const domain=document.getElementById("email-domain").value==="custom"
    ?document.getElementById("custom-domain").value.trim()
    :document.getElementById("email-domain").value;

    //이메일아이디와 도메인 결합
    const fullEmail = (emailId && domain) ? `${emailId}@${domain}` : "";
    document.getElementById("full-email").value = fullEmail;

}

function isValidEmailFormat(email) {    //정규식 검사
    const pattern = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    return pattern.test(email);
}

const form=document.querySelector("#sigunup-form");
form.addEventListener("submit",function(e){
    const email=document.getElementById("full-email").value;

    if(!isValidEmailFormat(email)){
        alert("이메일 형식이 올바르지 않습니다.");
        e.preventDefault(); //폼전송 막기
    }

})