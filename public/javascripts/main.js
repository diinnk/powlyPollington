let pollID;

const voteForm = document.forms.namedItem('voteForm');
voteForm.onsubmit = submitVote

const docPL = document.getElementById('pollList')
docPL.onchange = (_)  => {
    window.location.replace('./.?p='+docPL.value);
}

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
        (this.status === 200) ? showVoteStatusMessage("voteMessage", "Vote successful", 1750) : showVoteStatusMessage("voteMessage", "Vote FAILED, see logs/console", 5050);
        voteForm.reset();
    }
    return false;
}

function showVoteStatusMessage(divName, divText, delayLength) {
    let element = document.getElementById(divName);
    element.removeAttribute("hidden");
    element.innerHTML = divText
    delay(delayLength).then(() => element.setAttribute("hidden", "hidden"));
}

function delay(time) {
    return new Promise(resolve => setTimeout(resolve, time));
}