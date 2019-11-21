var showOfficiels = false;

var DIVISION_FOLDER = "division";

var DEFAULT_EXT = ".html";

var SALLE_FOLDER = "salle";

var RENCONTRE_FOLDER = "rencontres";

var CLASSEMENTS_FOLDER = "classements";

var ID_IFRAME_JOURNEE = "idIframeJournee";

var ID_DEFAULT_DIVISION = "idDefaultDivision";

var ID_DIVISION_SELECT = "idDivisionSelect";

var ID_IFRAME_CLASSEMENT = "idIframeClassements";

function getInternetExplorerVersion() {
    return -1;
}

function getIEMessage() {}

function openHere(b) {
    win = window.open("/here/here_popup.php?id=" + b, "Here", "toolbar=no,scrollbars=yes,resizable=yes,menubar=no, status=no, titlebar=no,width=870,height=700");
    win.focus();
}

function changerPoule(b) {
    if (b.selectedIndex != -1) {
        document.getElementById(ID_IFRAME_JOURNEE).src = b.options[b.selectedIndex].value;
    }
}

function changeSize(e) {
    if (e) {
        try {
            var f = e;
            var g = f.contentWindow || f.contentDocument;
            if (g.document) {
                g = g.document;
            }
            if (g.body) {
                if (g.body.id == "idIfType") {
                    if (g.body.clientHeight) {
                        if (g.body.clientHeight > f.height) {
                            f.height = g.body.clientHeight;
                        }
                    }
                    if (g.body.scrollHeight) {
                        if (g.body.scrollHeight > f.height) {
                            f.height = g.body.scrollHeight;
                        }
                    }
                } else {
                    f.height = 0;
                }
            }
        } catch (h) {
            var f = e;
            f.height = 0;
        }
    }
}

function creerPoules(c, d) {
    tdPoule = document.getElementById("idTdPoule");
    if (poules[c].length > 1) {
        tdPoule.innerHTML = "<select id='idPouleSelect' onchange='changerPoule(this)'></select>";
        pouleSelect = document.getElementById("idPouleSelect");
        pouleSelect.options.length = 0;
        s = document.getElementById(ID_DEFAULT_DIVISION).value;
        for (i = 0; i < poules[c].length; i++) {
            defaultSelected = false;
            if (d) {
                if (poules[c][i][0] == d) {
                    defaultSelected = true;
                }
            }
            pouleSelect.options[i] = new Option(poules[c][i][1], "journees/" + parseInt(c).toString(16) + parseInt(poules[c][i][0]).toString(16) + ".html?x=1&p=0&s=" + s, defaultSelected, defaultSelected);
        }
    } else {
        if (poules[c].length == 1) {
            tdPoule.innerHTML = poules[c][0][1];
        }
    }
    tdPoule.innerHTML += "<input type='hidden' id='idDefaultPoule' value='journees/" + parseInt(c).toString(16) + parseInt(poules[c][0][0]).toString(16) + ".html'>";
    return;
}

function creerCoupes(b) {
    tdCoupe = document.getElementById("idTdCoupe");
    if (coupes[b]) {
        if (coupes[b].length >= 1) {
            tdCoupe.innerHTML = "<select id='idCoupeSelect' onchange='changerCoupe(this)'></select>";
            coupeSelect = document.getElementById("idCoupeSelect");
            coupeSelect.options.length = 0;
            var c = "Saison rÃ©guliÃ¨re";
            coupeSelect.options[0] = new Option(c.toUpperCase(), "", false, false);
            for (i = 0; i < coupes[b].length; i++) {
                coupeSelect.options[i + 1] = new Option(coupes[b][i][1], coupes[b][i][0], false, false);
            }
        }
        document.getElementById("idDefaultCoupe").value = creerLienCoupe(coupes[b][0][0]);
    } else {
        if (tdCoupe) {
            tdCoupe.innerHTML = "";
        }
    }
    return;
}

function creerLienCoupe(b) {
    return RENCONTRE_FOLDER + "/" + b + DEFAULT_EXT;
}

function loadJournee() {
    url = document.location.href;
    acceptedParameters = [ "x", "p", "s" ];
    param = checkParameters(url, acceptedParameters);
    if (param[acceptedParameters[0]]) {
        document.getElementById("idFormJournee").action = "../" + parseInt(param.s).toString(16) + ".html";
        if (param.x == 1) {
            document.getElementById("idFormJournee").submit();
        }
        if (param.x == 0) {
            if (document.getElementById("p" + param.p)) {
                document.getElementById("p" + param.p).className = "tabloJournee-altern";
            }
        }
    }
}

function openJournee(f, d, e) {
    document.getElementById("idNumJournee").value = e;
    document.getElementById("idFormJournee").submit();
}

function checkParameters(o, k) {
    param = [];
    parametersOk = false;
    if (o.indexOf("?") != -1) {
        var l = o.split("?");
        if (l.length == 2) {
            var m = l[1].split("&");
            if (m.length == k.length) {
                parametersOk = true;
                for (var n = 0; n < m.length && parametersOk; n++) {
                    var h = m[n].split("=");
                    if (h.length == 2) {
                        add = false;
                        for (var j = 0; j < k.length && parametersOk; j++) {
                            if (k[j] == h[0] && n == j) {
                                param[h[0]] = h[1];
                                add = true;
                                break;
                            }
                        }
                        if (!add) {
                            parametersOk = false;
                            break;
                        }
                    } else {
                        parametersOk = false;
                        break;
                    }
                }
            }
        }
    }
    if (!parametersOk) {
        return [];
    }
    return param;
}

function loadDivision() {
    iFrameJournee = document.getElementById(ID_IFRAME_JOURNEE);
    iFrameRencontres = document.getElementById("idIframeRencontres");
    iFrameClassements = document.getElementById("idIframeClassements");
    obj = document.getElementById(ID_DEFAULT_DIVISION);
    url = document.location.href;
    acceptedParameters = [ "r", "d", "p" ];
    param = checkParameters(url, acceptedParameters);
    if (!param[acceptedParameters[0]]) {
        acceptedParameters = [ "r", "d" ];
        param = checkParameters(url, acceptedParameters);
    }
    if (param[acceptedParameters[0]] && param[acceptedParameters[1]]) {
        var d = [];
        for (var c = 0; c < acceptedParameters.length; c++) {
            d[c] = parseInt(param[acceptedParameters[c]]).toString(16);
        }
        if (param[acceptedParameters[2]]) {
            iFrameJournee.src = "journees/" + d[0] + d[1] + DEFAULT_EXT + "?x=0&p=" + param[acceptedParameters[2]] + "&s=" + obj.value;
            iFrameClassements.src = CLASSEMENTS_FOLDER + "/" + d[0] + d[1] + DEFAULT_EXT;
            iFrameRencontres.src = RENCONTRE_FOLDER + "/" + d[0] + d[1] + d[2] + DEFAULT_EXT;
        } else {
            iFrameJournee.src = "journees/" + d[0] + d[1] + DEFAULT_EXT + "?x=1&p=0&s=" + obj.value;
        }
        creerPoules(param[acceptedParameters[0]], param[acceptedParameters[1]]);
        creerCoupes(param[acceptedParameters[0]]);
        divisionSelect = document.getElementById(ID_DIVISION_SELECT);
        if (divisionSelect) {
            for (c = 0; c < divisionSelect.options.length; c++) {
                if (divisionSelect.options[c].value == param[acceptedParameters[0]]) {
                    divisionSelect.selectedIndex = c;
                }
            }
        }
    } else {
        if (obj) {
            if (obj.value) {
                creerPoules(obj.value);
                creerCoupes(obj.value);
                if (document.getElementById(ID_DIVISION_SELECT)) {
                    document.getElementById(ID_DIVISION_SELECT).selectedIndex = 0;
                }
                iFrameClassements.src = CLASSEMENTS_FOLDER + "/" + document.getElementById("idDefaultPoule").value + DEFAULT_EXT;
                iFrameJournee.src = document.getElementById("idDefaultPoule").value + "?x=1&p=0&s=" + obj.value;
            }
        }
    }
}

function loadOrganisme() {
    idOrg = document.getElementById("idOrganisme").value;
    document.getElementById("idIframeDirection").src = "direction/" + parseInt(idOrg).toString(16) + DEFAULT_EXT;
    document.getElementById("idIframeClubPro").src = "clubpro/" + parseInt(idOrg).toString(16) + DEFAULT_EXT;
    document.getElementById("idIframeDirectionPro").src = "direction/" + parseInt(idOrg).toString(16) + "_PRO" + DEFAULT_EXT;
    document.getElementById("idIframeChampionnat").src = "listechampionnats/" + parseInt(idOrg).toString(16) + DEFAULT_EXT;
    document.getElementById("idIframeChampionnat3x3").src = "listechampionnats3x3/" + parseInt(idOrg).toString(16) + DEFAULT_EXT;
    document.getElementById("idIframeCoupe").src = "listecoupes/" + parseInt(idOrg).toString(16) + DEFAULT_EXT;
    document.getElementById("idIframePlateau").src = "listeplateaux/" + parseInt(idOrg).toString(16) + DEFAULT_EXT;
    document.getElementById("idIframeOrganismeFils").src = "listeorganismes/" + parseInt(idOrg).toString(16) + DEFAULT_EXT;
    document.getElementById("idIframeAppartenance").src = "appartenance/" + parseInt(idOrg).toString(16) + DEFAULT_EXT;
    document.getElementById("idIframeEngagement").src = "engagements/" + parseInt(idOrg).toString(16) + DEFAULT_EXT;
    document.getElementById("idIframeSalle").src = SALLE_FOLDER + "/" + parseInt(idOrg).toString(16) + DEFAULT_EXT;
}

function changerDivision(b) {
    iFrameJournee = document.getElementById(ID_IFRAME_JOURNEE);
    if (b.options) {
        if (b.selectedIndex != -1) {
            creerPoules(b.options[b.selectedIndex].value);
            creerCoupes(b.options[b.selectedIndex].value);
            defaultDivision = document.getElementById(ID_DEFAULT_DIVISION);
            iFrameJournee.src = document.getElementById("idDefaultPoule").value + "?x=1&p=0&s=" + defaultDivision.value;
        }
    }
}

function creerLienClassement(b) {
    return CLASSEMENTS_FOLDER + "/" + parseInt(b).toString(16) + DEFAULT_EXT;
}

function loadCoupe() {
    defaultCoupe = document.getElementById("idDefaultCoupe");
    coupeSelect = document.getElementById("idCoupeSelect");
    for (i = 0; i < coupeSelect.options.length; i++) {
        if (coupeSelect.options[i].value == defaultCoupe.value) {
            coupeSelect.selectedIndex = i;
        }
    }
    iFrameClassements = document.getElementById("idIframeClassements");
    iFrameClassements.src = creerLienClassement(defaultCoupe.value);
    iFrameRencontre = document.getElementById("idIframeRencontres");
    iFrameRencontre.src = RENCONTRE_FOLDER + "/" + parseInt(defaultCoupe.value).toString(16) + DEFAULT_EXT;
}

function changerCoupe(b) {
    iFrameRencontre = document.getElementById("idIframeRencontres");
    iFrameJournee = document.getElementById(ID_IFRAME_JOURNEE);
    if (b.options) {
        if (b.selectedIndex == 0) {
            loadDivision();
        } else {
            iFrameRencontre.src = creerLienCoupe(parseInt(b.options[b.selectedIndex].value).toString(16));
            if (iFrameJournee) {
                iFrameJournee.height = 0;
            }
        }
    }
}

function afficherOfficiels() {
    iFrameRencontre = document.getElementById("idIframeRencontres");
    if (iFrameRencontre.src != "") {
        if (!showOfficiels) {
            iFrameRencontre.src = iFrameRencontre.src.split("?")[0] + "?showOffs=true";
            changeSize(iFrameRencontre);
            showOfficiels = true;
        } else {
            iFrameRencontre.src = iFrameRencontre.src.split("?")[0] + "?showOffs=false";
            showOfficiels = false;
            changeSize(iFrameRencontre);
        }
    }
}

function ouvrirOfficiels() {
    url = document.location.href;
    acceptedParameters = [ "showOffs" ];
    param = checkParameters(url, acceptedParameters);
    if (param[acceptedParameters[0]]) {
        i = 1;
        while (true) {
            show = "table-row";
            hide = "none";
            tr = document.getElementById("trOff" + i);
            if (tr) {
                if (param[acceptedParameters[0]] == "true") {
                    tr.style.display = show;
                } else {
                    if (param[acceptedParameters[0]] == "false") {
                        tr.style.display = hide;
                    }
                }
            } else {
                break;
            }
            i++;
        }
    }
}

function loadRencontresResultatsEquipe() {
    iFrameRencontres = document.getElementById("idIframeRencontres");
    iFrameClassements = document.getElementById(ID_IFRAME_CLASSEMENT);
    url = document.location.href;
    acceptedParameters = [ "r", "p", "d" ];
    if (url.indexOf("p=") == -1) {
        acceptedParameters = [ "r", "d" ];
    }
    competitionsSelect = document.getElementById("idCompetitionsSelect");
    param = checkParameters(url, acceptedParameters);
    if (param[acceptedParameters[0]]) {
        key = "";
        for (i = 0; i < acceptedParameters.length; i++) {
            key += parseInt(param[acceptedParameters[i]]).toString(16);
        }
        iFrameRencontres.src = DIVISION_FOLDER + "/" + key + DEFAULT_EXT;
        for (i = 0; i < competitionsSelect.options.length; i++) {
            if (competitionsSelect.options[i].value == key) {
                competitionsSelect.selectedIndex = i;
                break;
            }
        }
        iFrameClassements.src = "../" + CLASSEMENTS_FOLDER + "/" + classementsResultatsEquipe[competitionsSelect.options[competitionsSelect.selectedIndex].value] + DEFAULT_EXT;
    } else {
        if (competitionsSelect.options) {
            iFrameRencontres.src = DIVISION_FOLDER + "/" + competitionsSelect.options[0].value + DEFAULT_EXT;
            iFrameClassements.src = "../" + CLASSEMENTS_FOLDER + "/" + classementsResultatsEquipe[competitionsSelect.options[0].value] + DEFAULT_EXT;
        }
    }
}

function changerRencontresResultatsEquipe(b) {
    iFrameRencontres = document.getElementById("idIframeRencontres");
    iFrameClassements = document.getElementById(ID_IFRAME_CLASSEMENT);
    if (b.options) {
        if (b.selectedIndex != -1) {
            rencontres = b.options[b.selectedIndex].value;
            iFrameRencontres.src = DIVISION_FOLDER + "/" + rencontres + DEFAULT_EXT;
            iFrameClassements.src = "../" + CLASSEMENTS_FOLDER + "/" + classementsResultatsEquipe[rencontres] + DEFAULT_EXT;
            changeSize(iFrameRencontres);
            changeSize(iFrameClassements);
        }
    }
}

