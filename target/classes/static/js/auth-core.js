/**
 * CYBER-PORTFOLIO :: MODULE -> AUTH CORE
 */

document.addEventListener("DOMContentLoaded", () => {
    comprobarEstadoSesion();
});

function comprobarEstadoSesion() {
    const token = localStorage.getItem("token_admin");
    const loginDiv = document.getElementById("login-container");
    const panelDiv = document.getElementById("panel-layout");

    if (token) {
        if (loginDiv) loginDiv.style.display = "none";
        if (panelDiv) panelDiv.style.display = "flex";
        cargarModulo('dashboard');
    } else {
        if (loginDiv) loginDiv.style.display = "flex";
        if (panelDiv) panelDiv.style.display = "none";
        const mainContent = document.getElementById("main-content");
        if (mainContent) mainContent.innerHTML = "";
    }
}

function ejecutarLogin() {
    const email = document.getElementById("login-email").value.trim();
    const password = document.getElementById("login-password").value;
    const errorDiv = document.getElementById("login-error");

    if (!email || !password) {
        mostrarErrorLogin("Por favor, rellene todos los campos.");
        return;
    }

    if (errorDiv) errorDiv.style.display = "none";

    const btn = document.querySelector(".sec-btn-trigger");
    if (btn) { btn.innerText = "AUTENTICANDO..."; btn.disabled = true; }

    fetch("/api/v1/autenticacion/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    })
    .then(response => {
        if (!response.ok) throw new Error("Credenciales inválidas o acceso denegado.");
        return response.json();
    })
    .then(data => {
        localStorage.setItem("token_admin", data.token);
        comprobarEstadoSesion();
    })
    .catch(err => {
        mostrarErrorLogin(err.message);
    })
    .finally(() => {
        if (btn) { btn.innerText = "AUTENTICAR"; btn.disabled = false; }
    });
}

function evaluarEnterLogin(event) {
    if (event.key === 'Enter') ejecutarLogin();
}

function mostrarErrorLogin(mensaje) {
    const errorDiv = document.getElementById("login-error");
    if (errorDiv) {
        errorDiv.innerText = "⚠️ " + mensaje;
        errorDiv.style.display = "block";
    }
}

function logout() {
    if (window.siemIntervalId) { 
        clearInterval(window.siemIntervalId); 
        window.siemIntervalId = null; 
    }
    localStorage.removeItem("token_admin");
    comprobarEstadoSesion();
}