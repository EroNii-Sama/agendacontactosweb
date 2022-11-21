(async () => {

    const userInSession = getUserInSession();
    if (!userInSession) {
        window.location.href = 'login.html';
        return;
    }

    document.body.style.display = "block";

    document.getElementById("btn-salir").addEventListener("click", function (e) {
        e.preventDefault();
        salir();
    });

    document.getElementById("add-phone").addEventListener("click", addPhone);

    document.getElementById("btn-save").addEventListener("click", saveContact)

    document.getElementById("imageUploader").addEventListener("change", uploadImage);

    const urlParams = new URLSearchParams(window.location.search);
    const contactoId = urlParams.get("id");
    if (contactoId && !isNaN(contactoId)) {
        document.getElementById("page-title").innerHTML = "Editar Contacto";
        await loadContact(contactoId);
    } else {
        document.getElementById("page-title").innerHTML = "Nuevo Contacto";
    }

})();

async function loadContact(contactoId){
    const response = await fetch(`api/contacto/${contactoId}`);
    if (!response.ok) {
        alert("Error al cargar el contacto");
        return;
    }

    const data = await response.json();

    const nombre = document.querySelector("#nombreContacto");
    const email = document.querySelector("#email");
    const telefono = document.querySelector("#telefono");
    const contactoIdControl = document.querySelector("#contactoId");
    const imageId = document.querySelector("#imageId");

    nombre.value = data.nombreContacto;
    email.value = data.email;
    telefono.value = data.telefonos.length == 0 ? "" : data.telefonos[0].nroContacto;
    contactoIdControl.value = data.contactoId;
    imageId.value = data.imagenId;

    if (data.imagenId && data.imagenId > 0) {
        const miniatura = document.getElementById("miniatura");
        miniatura.src = "api/image/" + data.imagenId;
    }

    if (data.telefonos.length > 1) {
        telefono.dataset.id = data.telefonos[0].telefonoContactoId;
        let html = "";
        data.telefonos.slice(1, data.telefonos.length).forEach((telefono, index) => {
            html += createPhoneElement(index, telefono.nroContacto, telefono.telefonoContactoId);
        });

        const extraPhoneList = document.getElementById("phone-list");
        extraPhoneList.innerHTML = html;
    }
}

async function saveContact() {
    const contactoId = parseInt(document.querySelector("#contactoId").value);
    const nombre = document.querySelector("#nombreContacto").value.trim();
    const email = document.querySelector("#email").value.trim();
    const imageId = document.querySelector("#imageId").value;

    const validacionNombre = document.querySelector("#validation-name");
    const validacionEmail = document.querySelector("#validation-email");

    const msgError = document.querySelector("#msg-error");

    validacionNombre.style.display = "none"
    validacionEmail.style.display = "none"
    msgError.style.display = "none"

    let hasError = false;
    if (nombre === "") {
        hasError = true;
        validacionNombre.style.display = "block";
    }
    if (email === "") {
        hasError = true;
        validacionEmail.innerHTML = "El correo electrónico es requerido"
        validacionEmail.style.display = "block";
    } else if (!isEmailValid(email)) {
        hasError = true;
        validacionEmail.innerHTML = "El correo electrónico ingresado no es válido"
        validacionEmail.style.display = "block";
    }

    const telefonos = document.querySelectorAll(".telephone");

    telefonos.forEach(telefono => {
        if (telefono.value.trim() === "") {
            hasError = true;
            telefono.parentElement.nextElementSibling.style.display = "block";
        } else {
            telefono.parentElement.nextElementSibling.style.display = "none";
        }
    });

    if (hasError) {
        return;
    }

    const nroTelefonos = [...telefonos].map((telefono) => {
        return {
            "nroContacto": telefono.value.trim(),
            "telefonoContactoId": telefono.dataset.id,
            "contactoId": contactoId
        }
    });

    const userInSession = getUserInSession();
    const contacto = {
        usuarioId: userInSession.usuarioId,
        nombreContacto: nombre,
        email: email,
        telefonos: nroTelefonos,
        imagenId: imageId,
        contactoId: contactoId
    }

    const method = contactoId === 0 ? "POST" : "PUT";
    const response = await fetch("api/contacto", {
        method: method,
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        },
        body: JSON.stringify(contacto)
    });
    if (!response.ok) {
        console.log(response.error);
        msgError.innerHTML = "Error al guardar el contacto";
        msgError.style.display = "block";
        return;
    }
    const data = await response.json();
    if (!data) {
        console.log(response.error);
        msgError.innerHTML = "Error al guardar el contacto";
        msgError.style.display = "block";
    }
    window.location.href = "index.html?msg=ok_contact_saved";
}

function addPhone() {
    let phones = [...document.querySelectorAll(".extra-phone")];
    const phoneNumbers = phones.map(phone => phone.value);

    const index = phoneNumbers.length;
    const newPhoneElement = createPhoneElement(index);

    const container = document.getElementById("phone-list");
    container.innerHTML += newPhoneElement;

    phones = document.querySelectorAll(".extra-phone");
    for (let i in phoneNumbers) {
        const phoneNumber = phoneNumbers[i];
        phones[i].value = phoneNumber;
    }
}

function removePhone(index) {
    const phones = [...document.querySelectorAll(".extra-phone")];
    const phoneNumbers = phones.map(phoneElement => {
        return {
            "value": phoneElement.value,
            "id": phoneElement.dataset.id
        };
    });

    phoneNumbers.splice(index, 1);

    
    let html = "";
    for (let i in phoneNumbers) {
        const phoneData = phoneNumbers[i];
        html += createPhoneElement(i, phoneData.value, phoneData.id);
    }

    const container = document.getElementById("phone-list");
    container.innerHTML = html;
}

async function uploadImage() {
    const inputFile = document.getElementById("imageUploader");

    var data = new FormData();
    data.append("file", inputFile.files[0]);

    const response = await fetch("api/image", {
        method: "POST",
        body: data
    });

    if (!response.ok) {
        console.log(response.error);
        return;
    }
    const imageId = await response.json();

    document.getElementById("miniatura").src = "api/image/" + imageId;
    document.getElementById("imageId").value = imageId;
}

function createPhoneElement(index, value = "", id = 0) {
    return `<div class="mt-3">
                <div class="input-group">
                    <input type="text" class="form-control telephone extra-phone" value="${ value }" data-id="${ id }">
                    <div class="input-group-append">
                        <span class="input-group-text pointer" onclick="removePhone(${ index })">
                            <i class="fa-solid fa-trash"></i>
                        </span>
                    </div>
                </div>
                <div class="text-danger validation" style="display: none" id="validation-telefono">
                    El número de teléfono es requerido
                </div>
            </div>`
}

function isEmailValid(email) {
    const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}
