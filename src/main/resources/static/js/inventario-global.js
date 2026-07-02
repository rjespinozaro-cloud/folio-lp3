/**
 * CYBER-PORTFOLIO :: MODULE -> INVENTARIO GLOBAL & ANALYTICS
 * v3.0 - DARK MODERN EDITION (Sincronizado con variables CSS del Panel)
 */

// Objeto global para almacenar IDs en estado de edición (Modo PUT)
window.currentEditIds = {
    practica: null,
    certificacion: null,
    herramienta: null
};

/* ============================================================
   [A] PRÁCTICAS LABORALES
   ============================================================ */
function registrarPracticaLaboratorio(token) {
    const btn = document.getElementById("btn-guardar-practica");
    if (!btn) return;

    btn.onclick = function () {
        const titulo      = document.getElementById("prac-titulo").value.trim();
        const categoria   = document.getElementById("prac-categoria").value.trim();
        const description = document.getElementById("prac-descripcion").value.trim();
        const inputArchivo = document.getElementById("prac-archivo");

        if (!titulo || !description) {
            alert("⚠️ El título y la descripción son obligatorios.");
            return;
        }

        const formData = new FormData();
        formData.append("titulo", titulo);
        formData.append("descripcion", description);
        formData.append("categoria", categoria);
        if (inputArchivo && inputArchivo.files.length > 0) formData.append("archivo", inputArchivo.files[0]);

        const idEdicion = window.currentEditIds.practica;
        const url = idEdicion ? `/api/v1/practicas/${idEdicion}` : '/api/v1/practicas';
        const metodo = idEdicion ? 'PUT' : 'POST';

        btn.disabled = true;
        btn.innerText = idEdicion ? "UPDATING ASSET..." : "DEPLOYING ASSET...";

        fetch(url, {
            method: metodo,
            headers: { "Authorization": "Bearer " + token },
            body: formData
        })
        .then(res => { if (!res.ok) throw new Error(); return res.json(); })
        .then(() => {
            alert(idEdicion ? "🔄 Laboratorio actualizado con éxito." : "🚀 Laboratorio indexado con éxito.");
            document.getElementById("form-practicas").reset();
            window.currentEditIds.practica = null;
            btn.innerText = "DEPLOY LABORATORY";
            listarPracticasCrud(token);
        })
        .catch(() => alert("❌ Falló el despliegue del asset."))
        .finally(() => { btn.disabled = false; });
    };
}

function listarPracticasCrud(token) {
    const contenedor = document.getElementById("practicas-mosaico-container");
    if (!contenedor) return;

    contenedor.innerHTML = `<div class="siem-table-fallback" style="grid-column:1/-1;">Estableciendo enlace con el repositorio...</div>`;

    fetch('/api/v1/practicas', { headers: { "Authorization": "Bearer " + token } })
    .then(res => res.json())
    .then(data => {
        if (!data.length) {
            contenedor.innerHTML = '<div class="siem-table-fallback" style="grid-column:1/-1;">No hay laboratorios digitalizados en este nodo.</div>';
            return;
        }

        contenedor.innerHTML = "";
        data.forEach((p, i) => {
            const card = document.createElement("div");
            card.className = "stat-box-admin font-green";
            card.style.cssText = "display:flex; flex-direction:column; min-height:340px; overflow:hidden; position:relative; opacity:0; transform:translateY(14px);";

            const tipo = p.documentoTipo || "";
            let previewHtml = "";

            // Ajuste estético moderno de previsualizaciones (Mapeado a variables oscuras globales)
            if (p.rutaDocumento) {
                if (tipo.includes("pdf")) {
                    previewHtml = `
                        <div style="width:calc(100% + 3rem); height:130px; background:linear-gradient(135deg, var(--bg-root), var(--bg-card)); display:flex; flex-direction:column; align-items:center; justify-content:center; border-bottom:1px solid var(--border-dim); margin:-1.5rem -1.5rem 1rem;">
                            <span style="font-size:2rem;">📄</span>
                            <span style="font-size:0.7rem; color:var(--alert-danger); font-weight:700; margin-top:0.4rem; letter-spacing:1px; font-family:var(--font-ui);">PDF REPORT</span>
                        </div>`;
                } else {
                    previewHtml = `
                        <div style="width:calc(100% + 3rem); height:130px; background:url('${p.rutaDocumento}') center/cover no-repeat; border-bottom:1px solid var(--border-dim); margin:-1.5rem -1.5rem 1rem; position:relative;">
                            <div style="position:absolute; inset:0; background:linear-gradient(180deg, transparent 50%, var(--bg-surface));"></div>
                        </div>`;
                }
            } else {
                previewHtml = `
                    <div style="width:calc(100% + 3rem); height:130px; background:linear-gradient(135deg, var(--bg-root), var(--bg-surface)); display:flex; align-items:center; justify-content:center; border-bottom:1px solid var(--border-dim); margin:-1.5rem -1.5rem 1rem;">
                        <span style="font-size:0.7rem; color:var(--text-dim); letter-spacing:1px; font-family:var(--font-ui);">[ NO EVIDENCE ]</span>
                    </div>`;
            }

            const catColor = getCatColor(p.categoria);

            card.innerHTML = `
                ${previewHtml}
                <div style="flex-grow:1; padding-bottom:0.5rem;">
                    <span style="color:${catColor}; font-size:0.7rem; font-weight:700; text-transform:uppercase; letter-spacing:1px; font-family:var(--font-ui);">⚡ LAB // ${p.categoria || 'GENERAL'}</span>
                    <h3 style="font-size:1.1rem; margin:0.3rem 0 0.5rem; color:var(--text-bright); font-family:var(--font-ui); font-weight:700; line-height:1.3;">${p.titulo}</h3>
                    <p style="font-size:0.85rem; color:var(--text-muted); display:-webkit-box; -webkit-line-clamp:3; -webkit-box-orient:vertical; overflow:hidden; line-height:1.5; font-family:var(--font-ui);">${p.descripcion}</p>
                </div>
                <div style="margin-top:auto; display:flex; justify-content:space-between; align-items:center; border-top:1px solid var(--border-dim); padding-top:0.75rem; gap:8px;">
                    <div style="display:flex; gap:6px;">
                        ${p.rutaDocumento ? `<a href="${p.rutaDocumento}" target="_blank" style="color:var(--cyan); font-size:0.8rem; text-decoration:none; font-weight:600; font-family:var(--font-ui); align-self:center; margin-right:4px;">👁 VIEW</a>` : ''}
                        <button class="btn-delete-action btn-edit-lab" style="background:rgba(59,130,246,0.12) !important; color:var(--cyan) !important; border-color:rgba(59,130,246,0.3) !important;">EDIT</button>
                    </div>
                    <button class="btn-delete-action btn-purge-lab">🗑 PURGE</button>
                </div>`;

            card.querySelector(".btn-edit-lab").addEventListener("click", () => {
                window.currentEditIds.practica = p.id;
                document.getElementById("prac-titulo").value = p.titulo;
                document.getElementById("prac-categoria").value = p.categoria || "";
                document.getElementById("prac-descripcion").value = p.descripcion;
                
                const btnGuardar = document.getElementById("btn-guardar-practica");
                if (btnGuardar) btnGuardar.innerText = "UPDATE LABORATORY // MODE";
                window.scrollTo({ top: 0, behavior: "smooth" });
            });

            card.querySelector(".btn-purge-lab").addEventListener("click", () => {
                eliminarRegistro(`/api/v1/practicas/${p.id}`, token, () => listarPracticasCrud(token));
            });

            contenedor.appendChild(card);
            setTimeout(() => {
                if (card) {
                    card.style.transition = "opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1), transform 0.3s cubic-bezier(0.4, 0, 0.2, 1)";
                    card.style.opacity = "1";
                    card.style.transform = "translateY(0)";
                }
            }, Math.min(i * 50, 1000));
        });
    })
    .catch(() => {
        if (contenedor) contenedor.innerHTML = '<div class="error-table" style="grid-column:1/-1;">❌ Error al cargar el repositorio de prácticas.</div>';
    });
}

/* ============================================================
   [B] CERTIFICACIONES
   ============================================================ */
/* ============================================================
   [B] CERTIFICACIONES (PARCHADO PARA EVITAR IMÁGENES ROTAS)
   ============================================================ */
if (!window.currentEditIds) {
    window.currentEditIds = { practica: null, certificacion: null, herramienta: null };
} else if (!window.currentEditIds.hasOwnProperty('certificacion')) {
    window.currentEditIds.certificacion = null;
}

let certificacionesCache = [];

/**
 * 1. LISTAR ASSETS (Mosaico dinámico con Previews de Insignias desde /uploads)
 */
async function listarCertificacionesCrud(token) {
    const contenedor = document.getElementById("cert-mosaico-container");
    if (!contenedor) return;

    contenedor.innerHTML = `<div class="siem-table-fallback" style="grid-column:1/-1;">Validando hashes de certificación...</div>`;

    try {
        const response = await fetch('/api/v1/certificaciones', {
            method: 'GET',
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) throw new Error("Fallo en el enlace con el repositorio central.");

        // Sincronizar el caché local en memoria antes de renderizar
        certificacionesCache = await response.json();
        contenedor.innerHTML = "";

        if (!certificacionesCache.length) {
            contenedor.innerHTML = '<div class="siem-table-fallback" style="grid-column:1/-1;">No hay credenciales digitalizadas en este nodo.</div>';
            return;
        }

        certificacionesCache.forEach((c, i) => {
            const card = document.createElement("div");
            card.className = "stat-box-admin font-green";
            card.style.cssText = "display:flex; flex-direction:column; min-height:360px; overflow:hidden; position:relative; opacity:0; transform:translateY(14px);";

            // Enrutador de pasarela multimedia para la carpeta uploads
            let imgSrc = c.rutaImagen || c.imagenNombre || null;
            let previewHtml = "";

            if (imgSrc) {
                // 🛡️ SANITIZACIÓN CRÍTICA CONTRA DOBLE SLASH (Evita romper StrictHttpFirewall)
                if (!imgSrc.startsWith('http') && !imgSrc.startsWith('/api') && !imgSrc.startsWith('images/')) {
                    imgSrc = `/api/v1/evidencias/download/${imgSrc}`;
                }
                
                // Remueve barras dobles accidentales de la URL resultantes de la concatenación
                imgSrc = imgSrc.replace(/([^:]\/)\/+/g, "$1");

                previewHtml = `
                    <div style="width:calc(100% + 3rem); height:130px; background:url('${imgSrc}') center/contain no-repeat; background-color: rgba(0,0,0,0.15); border-bottom:1px solid var(--border-dim); margin:-1.5rem -1.5rem 1rem; position:relative;">
                        <div style="position:absolute; inset:0; background:linear-gradient(180deg, transparent 50%, var(--bg-surface));"></div>
                    </div>`;
            } else {
                previewHtml = `
                    <div style="width:calc(100% + 3rem); height:130px; background:linear-gradient(135deg, var(--bg-root), var(--bg-surface)); display:flex; align-items:center; justify-content:center; border-bottom:1px solid var(--border-dim); margin:-1.5rem -1.5rem 1rem;">
                        <span style="font-size:2.2rem;">🎓</span>
                    </div>`;
            }

            card.innerHTML = `
                ${previewHtml}
                <div style="flex-grow:1; padding-bottom:0.5rem;">
                    <span style="color:var(--lime); font-size:0.7rem; font-weight:700; text-transform:uppercase; letter-spacing:1px; font-family:var(--font-ui);">📜 ACCREDITED DIPLOMA</span>
                    <h3 style="font-size:1.1rem; margin:0.2rem 0 0.4rem; color:var(--text-bright); font-family:var(--font-ui); font-weight:700; line-height:1.3;">${c.nombre}</h3>
                    
                    <div style="margin-bottom:8px; font-family:var(--font-ui); font-size:0.85rem;">
                        <p style="color:var(--text-primary); margin:0;"><span style="color:var(--text-muted);">Issuer:</span> ${c.institucion}</p>
                        <p style="margin-top:4px; font-size:0.75rem;"><code>ID: ${c.codigoId || 'N/A'}</code></p>
                    </div>
                </div>
                <div style="margin-top:auto; display:flex; justify-content:space-between; align-items:center; border-top:1px solid var(--border-dim); padding-top:0.75rem; gap:8px;">
                    <div style="display:flex; gap:6px;">
                        ${c.urlValidacion ? `<a href="${c.urlValidacion}" target="_blank" style="color:var(--lime); font-size:0.8rem; text-decoration:none; font-weight:600; font-family:var(--font-ui); align-self:center; margin-right:4px;">👁 VALIDATE</a>` : '<span style="color:var(--text-dim); font-size:0.75rem; font-family:var(--font-ui); align-self:center; margin-right:4px;">🔒 INT</span>'}
                        <button class="btn-delete-action btn-edit-cert" style="background:rgba(59,130,246,0.12) !important; color:var(--cyan) !important; border-color:rgba(59,130,246,0.3) !important;">EDIT</button>
                    </div>
                    <button class="btn-delete-action btn-revoke-cert">🗑 REVOKE</button>
                </div>`;

            card.querySelector(".btn-edit-cert").addEventListener("click", () => {
                cargarDatosCertParaEditar(c.id);
            });

            card.querySelector(".btn-revoke-cert").addEventListener("click", () => {
                eliminarRegistro(`/api/v1/certificaciones/${c.id}`, token, () => listarCertificacionesCrud(token));
            });

            contenedor.appendChild(card);
            
            setTimeout(() => {
                if (card) {
                    card.style.transition = "opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1), transform 0.3s cubic-bezier(0.4, 0, 0.2, 1)";
                    card.style.opacity = "1";
                    card.style.transform = "translateY(0)";
                }
            }, Math.min(i * 50, 1000));
        });

    } catch (error) {
        console.error("❌ Error listando certificaciones:", error);
        contenedor.innerHTML = '<div class="siem-table-fallback" style="grid-column: 1 / -1; color:var(--alert-danger);">Error de infraestructura al conectar con el inventario de hashes.</div>';
    }
}

/**
 * 2. COLOCAR DATOS EN EL FORMULARIO DESDE EL CACHÉ LOCAL (EDICIÓN EN CALIENTE)
 */
function cargarDatosCertParaEditar(id) {
    const asset = certificacionesCache.find(c => c.id === id);
    if (!asset) return;

    window.currentEditIds.certificacion = asset.id; 

    document.getElementById("cert-nombre").value = asset.nombre;
    document.getElementById("cert-institucion").value = asset.institucion;
    document.getElementById("cert-codigo").value = asset.codigoId || "";
    document.getElementById("cert-url").value = asset.urlValidacion || "";
    
    document.getElementById("form-certificaciones").scrollIntoView({ behavior: 'smooth' });

    const btnSubmit = document.getElementById("btn-guardar-cert");
    if (btnSubmit) {
        btnSubmit.innerText = "OVERWRITE CERTIFICATE // MODE";
        btnSubmit.style.background = "var(--magenta)"; 
        btnSubmit.style.borderColor = "var(--magenta)";
        btnSubmit.style.color = "var(--text-bright)";
    }
}

/**
 * 3. ENVIAR DATOS TRANSMUTADOS (POST NUEVO / PUT EDICIÓN ASÍNCRONA)
 */
async function guardarCertificacion(token) {
    const form = document.getElementById("form-certificaciones");
    const btn = document.getElementById("btn-guardar-cert");
    if (!form || !btn) return;

    // Interceptor directo del evento Submit
    form.onsubmit = async function (e) {
        e.preventDefault(); 

        const nombre = document.getElementById("cert-nombre").value.trim();
        const institucion = document.getElementById("cert-institucion").value.trim();

        if (!nombre || !institucion) {
            alert("⚠️ El nombre de la credencial y la institución emisora son obligatorios.");
            return;
        }

        const formData = new FormData();
        formData.append("nombre", nombre);
        formData.append("institucion", institucion);
        formData.append("codigoId", document.getElementById("cert-codigo").value.trim());
        formData.append("urlValidacion", document.getElementById("cert-url").value.trim());

        // 🔑 SYNC EXCLUSIVO: Envío del multipart bajo el identificador "badge"
        const inputImg = document.getElementById("cert-imagen-file");
        if (inputImg && inputImg.files.length > 0) {
            formData.append("badge", inputImg.files[0]); 
        }

        const idEdicion = window.currentEditIds.certificacion;
        const url = idEdicion ? `/api/v1/certificaciones/${idEdicion}` : '/api/v1/certificaciones';
        const metodo = idEdicion ? 'PUT' : 'POST';

        btn.disabled = true;
        btn.innerText = idEdicion ? "UPDATING ASSET..." : "DEPLOYING ASSET...";

        try {
            const response = await fetch(url, {
                method: metodo,
                headers: { "Authorization": "Bearer " + token },
                body: formData
            });

            if (!response.ok) throw new Error("Rechazo en el procesamiento del payload multipart.");

            alert(idEdicion ? "🔄 Ecosistema de la certificación actualizado con éxito." : "🚀 Credencial indexada con éxito en la DB.");
            
            form.reset();
            window.currentEditIds.certificacion = null;
            
            btn.innerText = "MIGRAR CREDENCIAL A DB";
            btn.style.background = "";
            btn.style.borderColor = "";
            btn.style.color = "";

            await listarCertificacionesCrud(token);

        } catch (error) {
            console.error("🚨 Error crítico al guardar certificación:", error);
            alert("Error 400/500: Fallo en el pipeline perimetral al mutar el asset.");
        } finally {
            btn.disabled = false;
        }
    };
}

/**
 * 4. LIMPIEZA INTERNA DE INPUTS
 */
function resetCamposCert() {
    ["cert-nombre","cert-institucion","cert-codigo","cert-url","cert-imagen-file"].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = "";
    });
}
/* ============================================================
   [D] ANALÍTICA IA (Historial Enterprise de Consultas)
   ============================================================ */
function cargarAnaliticaIa(token) {
    const tbody = document.getElementById("stats-table-body");
    const positivoEl = document.getElementById("stats-feedback-ok");
    const totalEl = document.getElementById("stats-total-preguntas");

    if (!tbody) return;

    fetch('/api/v1/preguntas-ia/lista/historial', { 
        method: 'GET',
        headers: { 
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        } 
    })
    .then(res => {
        if (!res.ok) throw new Error("Fallo de enlace perimetral.");
        return res.json();
    })
    .then(data => {
        const totalPreguntas = data.length;
        const positivos = data.filter(p => p.calificacion === 'BUENA').length;
        
        if (totalEl) animarContador(totalEl, totalPreguntas);
        if (positivoEl) animarContador(positivoEl, positivos);

        tbody.innerHTML = totalPreguntas ? "" : '<tr><td colspan="4" class="siem-table-fallback">No hay interacciones registradas en el nodo.</td></tr>';

        data.forEach((p, i) => {
            const tr = document.createElement("tr");
            tr.style.cssText = "opacity:0; transition:opacity 0.25s cubic-bezier(0.4, 0, 0.2, 1);";
            
            const fechaLog = p.fechaHora ? new Date(p.fechaHora).toLocaleDateString() : new Date().toLocaleDateString();
            
            let calificacionHtml = "";
            if (p.calificacion === 'BUENA') {
                calificacionHtml = '<span class="success-tag-table">👍 Útil</span>';
            } else if (p.calificacion === 'MALA') {
                calificacionHtml = '<span class="error-tag-table">👎 No útil</span>';
            } else {
                calificacionHtml = '<span style="color:var(--text-dim); font-size:11px;">⏳ SIN CALIFICAR</span>';
            }

            tr.innerHTML = `
                <td><code>${fechaLog}</code></td>
                <td style="color:var(--text-primary); max-width:240px; white-space:normal; overflow-wrap:anywhere; font-family:var(--font-ui); font-size:0.9rem;">
                    ${p.preguntaEstudiante || 'Consulta vacía'}
                </td>
                <td style="color:var(--text-secondary); max-width:340px; white-space:normal; overflow-wrap:anywhere; font-size:0.85rem; line-height:1.5; font-family:var(--font-ui);">
                    ${p.respuestaIA || 'Sin respuesta generada.'}
                </td>
                <td style="text-align:center; vertical-align:middle; font-family:var(--font-ui);">${calificacionHtml}</td>
            `;

            tbody.appendChild(tr);

            setTimeout(() => {
                if (tr) tr.style.opacity = "1";
            }, Math.min(i * 25, 750));
        });
    })
    .catch(err => {
        console.error("🚨 Error crítico al leer el historial IA:", err);
        tbody.innerHTML = '<tr><td colspan="4" class="siem-table-fallback" style="color:var(--alert-danger);">❌ Fallo de infraestructura perimetral al procesar el stream de logs.</td></tr>';
    });
}

/* ============================================================
   [E] MANTENIMIENTO & MONITOREO DE SALUD
   ============================================================ */
function verificarSaludSistema(token) {
    fetch('/api/v1/salud', { headers: { "Authorization": "Bearer " + token } })
    .then(res => res.json())
    .then(health => {
        const set = (id, upVal) => {
            const el = document.getElementById(id);
            if (!el) return;
            const up = upVal === "UP";
            el.innerText = up ? "ONLINE" : "OFFLINE";
            el.className = up ? "sys-state-online" : "sys-state-sinking";
        };
        set("health-db", health.database);
        set("health-ai", health.iaEngine);
    }).catch(() => {
        const db = document.getElementById("health-db");
        const ai = document.getElementById("health-ai");
        if (db) { db.innerText = "OFFLINE"; db.className = "sys-state-sinking"; }
        if (ai) { ai.innerText = "OFFLINE"; ai.className = "sys-state-sinking"; }
    });
}

function ejecutarPurgaSystem(url, token) {
    if (!confirm("🚨 ¡ADVERTENCIA PERIMETRAL! ¿Seguro que desea vaciar este histórico? Esta acción es irreversible.")) return;

    fetch(url, { method: 'DELETE', headers: { "Authorization": "Bearer " + token } })
    .then(res => {
        if (res.ok) {
            alert("🧹 Purga realizada con éxito.");
            verificarSaludSistema(token);
        }
    });
}

/* ============================================================
   [G] UTILIDADES INTERNAS GLOBALES
   ============================================================ */
function animarContador(el, valorFinal) {
    if (!el || isNaN(valorFinal) || valorFinal === 0) return;
    const pasos = 30;
    const dur   = 600;
    let paso    = 0;
    const timer = setInterval(() => {
        paso++;
        el.innerText = Math.min(Math.round((valorFinal / pasos) * paso), valorFinal).toLocaleString();
        if (paso >= pasos) clearInterval(timer);
    }, dur / pasos);
}

function getCatColor(categoria) {
    if (!categoria) return 'var(--text-muted)';
    const c = categoria.toLowerCase();
    if (c.includes('red') || c.includes('pentest') || c.includes('exploit')) return '#f87171'; // Rojo suave
    if (c.includes('blue') || c.includes('soc') || c.includes('forense'))    return 'var(--cyan)';
    if (c.includes('osint') || c.includes('recon'))                           return '#fbbf24'; // Ámbar moderno
    if (c.includes('web'))                                                     return 'var(--magenta)';
    return 'var(--lime)';
}

function eliminarRegistro(url, token, callbackSuccess) {
    if (!confirm("⚠️ ¿Confirmas la revocación destructiva de este registro?")) return;
    
    fetch(url, { 
        method: 'DELETE', 
        headers: { "Authorization": "Bearer " + token } 
    })
    .then(async res => { 
        if (res.ok) {
            callbackSuccess(); 
        } else {
            let mensajeError = "Falló la comunicación con el endpoint de purga.";
            try {
                const errorData = await res.json();
                if (errorData && errorData.mensaje) mensajeError = errorData.mensaje;
            } catch (e) {}
            alert(`⚠️ Error (${res.status}): ${mensajeError}`);
        }
    })
    .catch(err => console.error("Fallo de red en borrado:", err));
}
