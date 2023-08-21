let pollID;

// const pollXMLHttp = new XMLHttpRequest();
// const pollListXMLHttp = new XMLHttpRequest();

// const pollListForm = document.forms.namedItem("pollList")
const voteForm = document.forms.namedItem('voteForm');
voteForm.onsubmit = submitVote


// pollListXMLHttp.onload = applyPollList
// pollXMLHttp.onload = applyPoll

// function loadPollList() {
//     pollListXMLHttp.open("GET", "./allPolls")
//     pollListXMLHttp.send()
// }

// function applyPollList() {
//     const pollListObj = JSON.parse(this.responseText);
//
//     let text = "<select name='pollList' id='pollList'>"
//
//     for (let x in pollListObj) {
//         let listPollId = pollListObj[x].pollId
//         text += "<option value="+ listPollId +">"+ pollListObj[x].pollTitle +" ("+ listPollId +")</option>"
//     }
//
//     text += "</select>"
//     document.getElementById("pollList").innerHTML = text;
// }

let docPL = document.getElementById('pollList')

docPL.onchange = (_)  => {
    window.location.replace('./.?p='+docPL.value);
}

// function loadPoll() {
//     pollXMLHttp.open("GET", "./viewPoll?p=" + pollID);
//     pollXMLHttp.send();
// }

// function applyPoll() {
//     const pollObj = JSON.parse(this.responseText);
//     const pollOptions = pollObj.pollOptions
//     const inputType = (!pollObj.allowMultipleSelections) ? 'checkbox' : 'radio';
//
//     let text = "<h2>" + pollObj.pollTitle + "</h2>"
//     text += "<h3>" + pollObj.pollDesc + "</h3>"
//
//     if (typeof pollObj.uniqueIndividualIdentifierLabel != "undefined") {
//         text += "<p>" + "<label for='uniqueIndividualIdentifier'>" + pollObj.uniqueIndividualIdentifierLabel + ": <sp><sp></label>"
//         text += "<input type='text' id='uniqueIndividualIdentifier' name='uniqueIndividualIdentifier'></p>"
//     }
//
//     for (let x in pollOptions) {
//         text += "<input type='"+ inputType +"' name='pollOptions' value="+ pollOptions[x].optionID +"> "+ pollOptions[x].optionName +"<br>"
//     }
//     document.getElementById("poll").innerHTML = text;
// }

function submitVote() {
    let xhr = new XMLHttpRequest();
    const formData = new FormData(voteForm);
    xhr.open('POST','/castVote')
    xhr.setRequestHeader("Content-Type", "application/json");

    const uniqueIndividualIdentifier = formData.get("uniqueIndividualIdentifier")
    const pollOptions = formData.getAll("pollOptions")

    let pollOptionsInt = [];
    const length = pollOptions.length;

    for (let i = 0; i < length; i++)
        pollOptionsInt.push(parseInt(pollOptions[i]));

    const jsonObj = {pollId: pollID, optionId: pollOptionsInt, uniqueIndividualIdentifier: uniqueIndividualIdentifier}
    xhr.send(JSON.stringify(jsonObj));

    xhr.onreadystatechange = function() {
        (this.status === 200) ? showVoteSuccessfulMessage("voteMessage", "Vote successful", 1750) : showVoteSuccessfulMessage("voteMessage", "Vote FAILED, see logs/console", 5050);
        voteForm.reset();
    }
    return false;
}

function showVoteSuccessfulMessage(divName, divText, delayLength) {
    let element = document.getElementById(divName);
    element.removeAttribute("hidden");
    element.innerHTML = divText
    delay(delayLength).then(() => element.setAttribute("hidden", "hidden"));
}

function delay(time) {
    return new Promise(resolve => setTimeout(resolve, time));
}