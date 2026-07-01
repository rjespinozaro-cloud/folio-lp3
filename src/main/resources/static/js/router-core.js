/**
 * CYBER-PORTFOLIO :: MODULE -> ROUTER CORE (OPTIMIZADO & CORREGIDO)
 */

window.siemIntervalId = null;

function cargarModulo(nombreModulo) {
    if (window.siemIntervalId) {
        clearInterval(window.siemIntervalId);
        window.siemIntervalId = null;
    }

    const token = localStorage.getItem("token_admin");
    if (!token) { comprobarEstadoSesion(); return; }

    actualizarMenuActivo(nombreModulo);

    const contenedor = document.getElementById("main-content");
    if (contenedor) {
        contenedor.style.opacity = "0";
        contenedor.style.transform = "translateY(6px)";
    }

    // El archivo cargado siempre será exactamente herramientas.html si se pasa 'herramientas'
    fetch(`/html/modulos-admin/${nombreModulo}.html`, {
        headers: { "Authorization": "Bearer " + token }
    })
    .then(response => {
        if (response.status === 401 || response.status === 403) throw new Error("Sesión expirada.");
        return response.text();
    })
    .then(htmlFragmento => {
        if (contenedor) {
            contenedor.innerHTML = htmlFragmento;
            requestAnimationFrame(() => {
                contenedor.style.transition = "opacity 0.25s ease, transform 0.25s ease";
                contenedor.style.opacity = "1";
                contenedor.style.transform = "translateY(0)";
            });
            ejecutarInicializadorModulo(nombreModulo);
        }
    })
    .catch(err => {
        console.error("Fallo de ruteo:", err);
        logout();
    });
}

function actualizarMenuActivo(moduloName) {
    document.querySelectorAll(".sec-sidebar-menu button").forEach(btn => {
        const oc = btn.getAttribute("onclick") || "";
        btn.classList.toggle("active", oc.includes(`'${moduloName}'`));
    });
}

function ejecutarInicializadorModulo(modulo) {
    const token = localStorage.getItem("token_admin");
    if (!token) return;

    console.log(`📡 Pipeline inicializado para: [${modulo.toUpperCase()}]`);

    switch (modulo) {
        case 'dashboard':
            cargarTelemetriaDashboard(token);
            window.siemIntervalId = setInterval(() => {
                if (document.getElementById("siem-table-body")) {
                    cargarTelemetriaDashboard(token);
                } else {
                    clearInterval(window.siemIntervalId);
                    window.siemIntervalId = null;
                }
            }, 3000);
            break;

        case 'gestion-practicas':
            if (typeof listarPracticasCrud === 'function') {
                listarPracticasCrud(token);
                registrarPracticaLaboratorio(token);
            }
            break;

        case 'certificaciones-admin':
            if (typeof listarCertificacionesCrud === 'function') {
                listarCertificacionesCrud(token);
            }
            // 🛡️ CORRECCIÓN DE FLUJO: Interceptamos el Submit del Formulario igual que en Herramientas
            setTimeout(() => {
                const formCert = document.getElementById("form-certificaciones");
                if (formCert && typeof guardarCertificacion === 'function') {
                    formCert.onsubmit = function(e) {
                        e.preventDefault();
                        guardarCertificacion(token);
                    };
                }
                // Si el botón está fuera del form o requiere un trigger alternativo por herencia:
                const btnGuardarCert = document.getElementById("btn-guardar-cert");
                if (btnGuardarCert) {
                    btnGuardarCert.onclick = () => {
                        guardarCertificacion(token);
                    };
                }
            }, 150);
            break;

        case 'herramientas':
        case 'herramientas-admin':
            if (typeof listarHerramientasCrud === 'function') {
                listarHerramientasCrud(token); 
            }
            
            setTimeout(() => {
                if (typeof cargarPilaresEnFormulario === 'function') {
                    cargarPilaresEnFormulario(token);
                }

                const formTool = document.getElementById("form-herramientas");
                if (formTool && typeof guardarHerramienta === 'function') {
                    formTool.onsubmit = function(e) {
                        e.preventDefault();
                        guardarHerramienta(token);
                    };
                }
            }, 150);
            break;

        case 'estadisticas':
            if (typeof cargarAnaliticaIa === 'function') {
                cargarAnaliticaIa(token);
                
                window.siemIntervalId = setInterval(() => {
                    if (document.getElementById("stats-table-body")) {
                        cargarAnaliticaIa(token);
                    } else {
                        clearInterval(window.siemIntervalId);
                        window.siemIntervalId = null;
                    }
                }, 45000);
            }
            break;

        // 🔥 ¡CORRECCIÓN AQUÍ!: Inyección y enganche dinámico para la pestaña de la IA
        case 'ia-config':
            if (typeof window.inicializarConfiguracionIA === 'function') {
                window.inicializarConfiguracionIA(token);
            } else {
                console.warn("⚠️ Advertencia: inicializarConfiguracionIA no está declarada en el entorno global.");
            }
            break;

        // 🛡️ CORRECCIÓN SEVERA: Retardo controlado para inyección segura de eventos de purga
        case 'administracion':
            if (typeof verificarSaludSistema === 'function') {
                verificarSaludSistema(token);
            }
            
            setTimeout(() => {
                const btnPurgarSiem = document.getElementById("btn-purgar-siem");
                const btnPurgarChat = document.getElementById("btn-purgar-chat");
                
                if (btnPurgarSiem) {
                    btnPurgarSiem.onclick = () => {
                        if (confirm("⚠️ ¿Deseas purgar los logs del SIEM en MariaDB?")) {
                            ejecutarPurgaSystem('/api/v1/auditoria/logs/purgar', token);
                        }
                    };
                }
                if (btnPurgarChat) {
                    btnPurgarChat.onclick = () => {
                        if (confirm("🔥 ¿Deseas purgar todo el historial RAG de la IA?")) {
                            ejecutarPurgaSystem('/api/v1/preguntas-ia/purgar', token);
                        }
                    };
                }
            }, 150); // Da tiempo a que el DOM dibuje los botones
            break;
    }
}