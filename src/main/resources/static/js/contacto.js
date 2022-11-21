(async function(){

    const userInSession = getUserInSession();
    if (!userInSession) {
        window.location.href = 'login.html';
        return;
    }

    document.body.style.display = "block";

    const urlParams = new URLSearchParams(window.location.search);
    const msg = urlParams.get("msg");
    if (msg && msg === "ok_contact_saved") {
        document.getElementById("ok_contact_saved").style.display = "block";
        setTimeout(() => {
            document.getElementById("ok_contact_saved").style.display = "none";
        }, 5000)
    }

    document.getElementById("btn-salir").addEventListener("click", function (e) {
        e.preventDefault();
        salir();
    });

    await cargarContactos();
})();

async function cargarContactos() {
    const userInSession = getUserInSession();
    const response = await fetch(`/api/contacto/usuario/${userInSession.usuarioId}`);
    if (!response.ok) {
        return;
    }

    const listOfContacts = await response.json();
    mostrarContactos(listOfContacts);
}

function mostrarContactos(listOfContacts){
    const contactosHtml = document.getElementById("contactos");

    if (listOfContacts.length === 0) {
        contactosHtml.innerHTML =
            `<div class="col-12 text-center mb-5">
                No tiene contactos registrados. Presione el botón "Nuevo contacto" para registrar uno nuevo.
            </div>`;
        return;
    }        

    let html = "";
    for (let i in listOfContacts) {
        const obj = listOfContacts[i];
        obj.telefono = obj.telefonos.length == 0 ? "" : obj.telefonos[0].nroContacto;
        html += getContactoInHtml(obj);
    }
    contactosHtml.innerHTML = html;
}

async function eliminarContacto(contactoId) {
    if (!confirm('¿Esta seguro que desea eliminar el contacto seleccionado')) {
        return;
    }

    const response = await fetch(`api/contacto/${contactoId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        }
    });
    if (!response.ok) {
        alert("Error al eliminar el contacto!!!")
        return;
    }
    cargarContactos();
}
    

function getContactoInHtml(obj) {
    const imageUrl = !obj.imagenId || obj.imagenId === 0 ? 'img/User-icon.png' : `api/image/${obj.imagenId}`;
    return `<div class="col-md-3">
                <div class="card mb-4">
                    <div class="text-center mt-3 mb-3">
                        <img src="${ imageUrl}" class="card-img-top contacto-img" alt="${obj.nombreContacto }">
                    </div>
                    <div class="card-body">
                        <h5 class="card-title">${ obj.nombreContacto }</h5>
                        <div class="card-text mb-2">
                            <div class="mb-1"><strong>Teléfono:</strong> ${ obj.telefono }</div>
                            <div>
                                <strong>Correo electrónico:</strong>
                                ${ obj.email }
                            </div>
                        </div>
                        <a href="contacto-frm.html?id=${ obj.contactoId }" class="btn btn-primary btn-block">Editar</a>
                        <button type="button" class="btn btn-danger btn-block" onclick="eliminarContacto(${ obj.contactoId })">Eliminar</button>
                    </div>
                </div>
            </div>`;
}
