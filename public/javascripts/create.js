const optionsElem = document.getElementById('options');
const createForm = document.forms.namedItem('createForm');

function add(){
    let label_tags = optionsElem.getElementsByTagName('label');
    let newIdValue = Number(label_tags.length)+1

    let newInput = document.createElement('input');
    let newLabel = document.createElement('label');

    newLabel.setAttribute('for', newIdValue.toString())
    newLabel.innerHTML = newIdValue+":"
    newInput.setAttribute('type','text');
    newInput.setAttribute('name', 'pollOptions')
    newInput.setAttribute('id', newIdValue.toString());

    optionsElem.appendChild(newLabel);
    optionsElem.appendChild(newInput);

    if (newIdValue > 2){
        let removeButton = document.getElementById("removeButton");
        removeButton.removeAttribute("hidden")
    }
}

function remove(){
    let label_tags = optionsElem.getElementsByTagName('label');
    let input_tags = optionsElem.getElementsByTagName('input');
    let labelLength = label_tags.length

    if(labelLength > 2) {
        optionsElem.removeChild(label_tags[(label_tags.length) - 1]);
        optionsElem.removeChild(input_tags[(input_tags.length) - 1]);
        if (labelLength <= 3){
            document.getElementById("removeButton").setAttribute("hidden", "hidden");
        }
    }
}

function checkOnSubmitButton() {
    const optionInputs = optionsElem.getElementsByTagName("input")
    const boolArray = [
        document.getElementById("pollTitle").value !== "",
        document.getElementById("pollDesc").value !== "",
        optionInputs.namedItem("1").value !== "",
        optionInputs.namedItem("2").value !== ""
    ]

    const submitNewPollButtonElem = document.getElementById("submitNewPollButton")
    const hiddenValueForSubmitButton = submitNewPollButtonElem.getAttribute("hidden")

    if (boolArray.every(Boolean)){
        submitNewPollButtonElem.removeAttribute("hidden");
    } else {
        if (hiddenValueForSubmitButton !== "" || hiddenValueForSubmitButton === null) {
            submitNewPollButtonElem.getAttribute("hidden");
            submitNewPollButtonElem.setAttribute("hidden", "hidden");
        }
    }
}

function submitNewPoll() {
    const formData = new FormData(createForm);
    const pollTitle = formData.get("pollTitle")
    const pollDesc = formData.get("pollDesc")
    const uniqueIndividualIdentifierLabel = formData.get("uniqueID")
    const options = formData.getAll("pollOptions")
    const optionsLen = options.length
    const allowMultipleSelections = formData.get("multiSelections") === "on"
    const allowMultipleIndividualVoteActions = formData.get("multiVotes") === "on"

    let pollOptions = [];
    for (let i = 0; i < optionsLen; i++) {
        const foundValue = options[i]
        if (foundValue !== "") pollOptions.push(options[i])
    }

    let jsonObj = {
        pollTitle: pollTitle,
        pollDesc: pollDesc,
        allowMultipleSelections: allowMultipleSelections,
        allowMultipleIndividualVoteActions: allowMultipleIndividualVoteActions,
        pollOptions: pollOptions
    }
    if (uniqueIndividualIdentifierLabel !== "") jsonObj.uniqueIndividualIdentifierLabel = uniqueIndividualIdentifierLabel

    finalSubmitOfNewPoll(JSON.stringify(jsonObj))

}

function finalSubmitOfNewPoll(payload) {
    let xhr = new XMLHttpRequest();
    xhr.open('POST','/createPoll')
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(payload);

    xhr.onreadystatechange = function() {
        if (this.status === 200) {
            if (this.responseText !== "") redirect(this.responseText)
        }
        // createForm.reset();
    }
    return false;
}

function redirect(responseText) {
    const resp = JSON.parse(responseText)
    window.location.replace('/?p='+resp.createdID)
}