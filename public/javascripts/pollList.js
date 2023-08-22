const docPL = document.getElementById('pollList')
docPL.onchange = (_)  => {
    window.location.replace('?p='+docPL.value);
}