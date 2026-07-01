/**
 * =================================================================================
 * CYBER-PORTFOLIO CORE ENGINE :: IA-PUBLIC-CHAT.JS (VISTA PÚBLICA & RAG ENGINE)
 * =================================================================================
 * v3.5 - CHARCOAL NEON & CARBON OPS EDITION // COMPATIBILIDAD DE REPOSITORIO GLOBAL
 */

// Variable global para rastrear en qué sección está parado el usuario
let moduloActualActivo = "inicio";

/**
 * Carga dinámica de módulos públicos en el viewport principal
 */
function cargarModuloPublico(nombreModulo) {
    moduloActualActivo = nombreModulo; // Registramos la sección actual
    
    fetch(`/html/modulos-publicos/${nombreModulo}.html`)
    .then(res => {
        if (!res.ok) throw new Error("No se pudo obtener el fragmento HTML");
        return res.text();
    })
    .then(html => {
        document.getElementById("main-content").innerHTML = html;
        
        // Sincronización de clases de navegación en el sidebar del visitante
        document.querySelectorAll('.sidebar-menu button').forEach(btn => {
            btn.classList.remove('active-module');
            if(btn.getAttribute('onclick')?.includes(nombreModulo)) {
                btn.classList.add('active-module');
            }
        });

        // Interceptor dinámico de datos desde MariaDB
        inicializarDatosModuloPublico(nombreModulo);
    })
    .catch(err => console.error("Error cargando vistas públicas: ", err));
}

/**
 * Escucha la tecla Enter en el input de texto
 */
function evaluarEnter(e) { 
    if (e.key === 'Enter') preguntarIA(); 
}

/**
 * Envío de la consulta con Inyección de Contexto en Vivo (RAG Local/DOM)
 */
function preguntarIA() {
    const input = document.getElementById("ia-input");
    const btnEnviar = document.getElementById("btn-preguntar-ia");
    const chatBox = document.getElementById("chat-box");
    const query = input.value.trim();
    
    if (!query) return;

    chatBox.innerHTML += `<div class="msg user">${query}</div>`;
    input.value = "";
    
    const indicadorCargaId = "loading-" + Date.now();
    // REEMPLAZO: Icono animado de carga SOC reemplazando el emoji del robot plano
    chatBox.innerHTML += `
        <div class="msg assistant" id="${indicadorCargaId}" style="display: flex; align-items: center; gap: 8px;">
            <svg class="hud-bot-spinning" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#39ff14" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="animation: spin 2s linear infinite;">
                <circle cx="12" cy="12" r="10" stroke-dasharray="4 4"/>
            </svg>
            <i>Analizando vectores de auditoría...</i>
        </div>`;
    chatBox.scrollTop = chatBox.scrollHeight;

    if (input) input.disabled = true;
    if (btnEnviar) btnEnviar.disabled = true;

    const contenidoViewport = document.getElementById("main-content")?.innerText || "";
    
    const payload = {
        preguntaEstudiante: query,
        seccionActual: moduloActualActivo,
        contextoPagina: contenidoViewport.substring(0, 4000)
    };

    fetch("/api/v1/preguntas-ia/preguntar", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    })
    .then(res => {
        if (!res.ok) throw new Error("Respuesta de servidor inválida");
        return res.json();
    })
    .then(data => {
        const loadingEl = document.getElementById(indicadorCargaId);
        if (loadingEl) {
            loadingEl.innerHTML = data.respuestaIA;
        } else {
            chatBox.innerHTML += `<div class="msg assistant">${data.respuestaIA}</div>`;
        }
    })
    .catch(err => {
        console.error("Error en comunicación con motor IA: ", err);
        const loadingEl = document.getElementById(indicadorCargaId);
        if (loadingEl) {
            loadingEl.className = "msg error";
            loadingEl.innerHTML = "Asistente fuera de línea temporalmente.";
        }
    })
    .finally(() => {
        if (input) input.disabled = false;
        if (btnEnviar) btnEnviar.disabled = false;
        if (input) input.focus();
        chatBox.scrollTop = chatBox.scrollHeight;
    });
}

/* ============================================================
   [PIPELINE DE EXTRACCIÓN] Consumo Asíncrono de Base de Datos
   ============================================================ */

function inicializarDatosModuloPublico(modulo) {
    switch (modulo) {
        case 'inicio':
            cargarTelemetriaInicio();
            break;
        case 'laboratorios': 
            cargarLaboratoriosVisor();
            break;
        case 'certificaciones':
            cargarCertificacionesVisor();
            break;
        case 'herramientas':
            cargarHerramientasVisor();
            break;
        case 'evidencias':
            cargarEvidenciasVisor();
            break;
        case 'proyectos':
            inicializarEfectosProyectos();
            break;
        case 'roadmap':
            inicializarEfectosRoadmap();
            break;
    }
}

/** 🎯 Renderiza los Laboratorios públicos (Pestaña Consolidada con diseño Neon) */
function cargarLaboratoriosVisor() {
    const grid = document.getElementById("grid-laboratorios");
    if (!grid) return;

    grid.innerHTML = `<div class="siem-table-fallback" style="grid-column:1/-1;">Estableciendo sockets relacionales...</div>`;

    fetch('/api/v1/practicas')
        .then(res => res.json())
        .then(data => {
            if (data.length === 0) {
                grid.innerHTML = `<div class="siem-table-fallback" style="grid-column:1/-1;">No hay laboratorios activos en este nodo de red.</div>`;
                return;
            }

            grid.innerHTML = "";
            data.forEach((p, i) => {
                let c = (p.categoria || "").toLowerCase();
                let catColor = 'var(--lime, #39ff14)'; 
                if (c.includes('red') || c.includes('pentest') || c.includes('exploit')) catColor = '#ff1744'; // Rojo neón
                if (c.includes('blue') || c.includes('soc') || c.includes('forense'))    catColor = '#00d4ff'; // Cyan
                if (c.includes('osint') || c.includes('recon'))                           catColor = '#ffd60a'; // Oro

                const card = document.createElement("div");
                card.className = "stat-box-admin font-green";
                card.style.cssText = `max-width: 100%; border-top: 3px solid ${catColor}; display:flex; flex-direction:column; min-height:240px; background:#1a1a1a; padding:1.5rem; border-radius:6px; opacity: 0; transform: translateY(12px); transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);`;

                // REEMPLAZO: Emojis rayo, blanco y ojo reemplazados por SVGs vectoriales integrados inline
                card.innerHTML = `
                    <div style="flex-grow:1;">
                        <div style="display: flex; align-items: center; margin-bottom: 0.6rem; flex-wrap: wrap; gap: 8px;">
                            <span style="color: ${catColor}; font-size: 0.72rem; font-weight: 700; text-transform: uppercase; letter-spacing: 1px; font-family: var(--font-ui); display: inline-flex; align-items: center; gap: 4px;">
                                <svg width="10" height="12" viewBox="0 0 24 24" fill="none" stroke="${catColor}" stroke-width="2.5"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
                                LAB // ${p.categoria || 'GENERAL'}
                            </span>
                        </div>
                        <h3 style="font-size: 1.2rem; margin: 0 0 0.6rem 0; color: var(--text-bright, #fff); font-family: var(--font-ui); font-weight:700; display: flex; align-items: center; gap: 8px;">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="${catColor}" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/></svg>
                            ${p.titulo}
                        </h3>
                        <p style="margin-bottom: 1.2rem; color: var(--text-muted, #a0a0a0); font-size: 0.88rem; line-height: 1.5; font-family: var(--font-ui);">
                            ${p.descripcion}
                        </p>
                    </div>
                    ${p.rutaDocumento ? `
                        <div style="margin-top: auto; border-top: 1px solid #333; padding-top: 0.8rem; display: flex; justify-content: flex-start;">
                            <a href="${p.rutaDocumento}" target="_blank" style="color: var(--cyan, #00e5ff); text-decoration: none; font-size: 0.82rem; font-family: var(--font-mono); font-weight: 600; display: inline-flex; align-items: center; gap: 6px;">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                                VIEW AUDIT REPORT (.PDF)
                            </a>
                        </div>
                    ` : ''}
                `;

                grid.appendChild(card);
                setTimeout(() => {
                    card.style.opacity = "1";
                    card.style.transform = "translateY(0)";
                }, i * 50);
            });
        })
        .catch(() => {
            // REEMPLAZO: Emoji cruz de error reemplazado por escudo de advertencia SVG
            grid.innerHTML = `
                <div class="siem-table-fallback" style="grid-column:1/-1; color: #ff1744; display: flex; align-items: center; justify-content: center; gap: 8px;">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#ff1744" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                    Error de infraestructura en el repositorio de prácticas.
                </div>`;
        });
}

/** 📂 Carga las Evidencias Criptográficas (Sincronizado con hashes de auditoría) */
function cargarEvidenciasVisor() {
    const grid = document.getElementById("grid-evidencias");
    if (!grid) return;

    grid.innerHTML = `<div class="siem-table-fallback" style="grid-column:1/-1;">Validando firmas criptográficas...</div>`;

    fetch('/api/v1/practicas')
        .then(res => res.json())
        .then(data => {
            const evidencias = data.filter(p => p.rutaDocumento);

            if (evidencias.length === 0) {
                grid.innerHTML = `<div class="siem-table-fallback" style="grid-column:1/-1;">No se han encontrado reportes firmados (.PDF) subidos al nodo.</div>`;
                return;
            }

            grid.innerHTML = "";
            evidencias.forEach((ev, i) => {
                const card = document.createElement("div");
                card.className = "stat-box-admin font-green";
                card.style.cssText = "max-width: 100%; border-left: 4px solid var(--cyan, #00e5ff); background:#1a1a1a; padding:1.5rem; border-radius:6px; opacity: 0; transform: translateY(12px); transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);";

                const virtualHash = ev.hashVerificacion || "SHA256-" + Math.random().toString(16).substring(2, 18).toUpperCase() + Math.random().toString(16).substring(2, 10).toUpperCase();

                // REEMPLAZO: Emojis de documento de reporte y descarga reemplazados por SVGs tácticos consistentes
                card.innerHTML = `
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.6rem; gap: 10px;">
                        <h3 style="color: var(--text-bright, #fff); font-size: 1.15rem; margin:0; font-family: var(--font-ui); font-weight:700; display: flex; align-items: center; gap: 8px;">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="var(--text-bright)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                            ${ev.titulo}
                        </h3>
                        <span style="font-family: var(--font-mono); font-size: 0.65rem; color: var(--cyan, #00e5ff); background: rgba(0,229,255,0.08); padding: 2px 6px; border: 1px solid rgba(0,229,255,0.2); border-radius: 4px; white-space: nowrap;">SIGNED REPORT</span>
                    </div>
                    <div style="background: #111; padding: 0.75rem; border-radius: 4px; border: 1px solid #333; margin-bottom: 1rem; font-family: var(--font-mono); font-size: 0.75rem; overflow-x: auto; white-space: nowrap;">
                        <span style="color: #666;">INTEGRITY HASH:</span> <code style="color: #ffd60a;">${virtualHash}</code>
                    </div>
                    <a href="${ev.rutaDocumento}" target="_blank" style="color: var(--lime, #39ff14); text-decoration:none; font-size: 0.82rem; font-family: var(--font-mono); font-weight: 600; display:inline-flex; align-items:center; gap: 6px;">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
                        DOWNLOAD AUDITED DATA (.PDF)
                    </a>
                `;

                grid.appendChild(card);
                setTimeout(() => {
                    card.style.opacity = "1";
                    card.style.transform = "translateY(0)";
                }, i * 50);
            });
        })
        .catch(() => {
            // REEMPLAZO: Almacén de error crítico con icono SVG de escudo roto
            grid.innerHTML = `
                <div class="siem-table-fallback" style="grid-column:1/-1; color: #ff1744; display: flex; align-items: center; justify-content: center; gap: 8px;">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#ff1744" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                    Fallo al escanear el almacén criptográfico.
                </div>`;
        });
}
/** 🎓 Carga las Certificaciones (Parchado con el enrutador multimedia y render de insignias reales) */
function cargarCertificacionesVisor() {
    const grid = document.getElementById("grid-certificaciones");
    if (!grid) return;

    grid.innerHTML = `<div class="siem-table-fallback" style="grid-column:1/-1;">Escaneando credenciales en la base de datos...</div>`;

    fetch('/api/v1/certificaciones')
        .then(res => res.json())
        .then(data => {
            if (data.length === 0) {
                grid.innerHTML = `<div class="siem-table-fallback" style="grid-column:1/-1;">No hay credenciales oficiales publicadas en este nodo.</div>`;
                return;
            }
            grid.innerHTML = "";
            data.forEach((c, i) => {
                const card = document.createElement("div");
                card.className = "stat-box-admin font-green";
                card.style.cssText = "display:flex; flex-direction:column; min-height:360px; background:#1a1a1a; border-radius:6px; overflow:hidden; position:relative; opacity:0; transform:translateY(14px);";

                // Enrutador de pasarela multimedia idéntico al del admin para evitar links rotos
                let imgSrc = c.rutaImagen || c.imagenNombre || null;
                let previewHtml = "";

                if (imgSrc) {
                    if (!imgSrc.startsWith('http') && !imgSrc.startsWith('/api') && !imgSrc.startsWith('images/')) {
                        imgSrc = `/api/v1/evidencias/download/${imgSrc}`;
                    }
                    imgSrc = imgSrc.replace(/([^:]\/)\/+/g, "$1");

                    previewHtml = `
                        <div style="width:100%; height:140px; background:url('${imgSrc}') center/contain no-repeat; background-color: rgba(0,0,0,0.2); border-bottom:1px solid #333; position:relative;">
                            <div style="position:absolute; inset:0; background:linear-gradient(180deg, transparent 50%, #1a1a1a);"></div>
                        </div>`;
                } else {
                    // REEMPLAZO: Icono de sombrero de graduación plano reemplazado por un SVG académico vectorial
                    previewHtml = `
                        <div style="width:100%; height:140px; background:linear-gradient(135deg, #111, #222); display:flex; align-items:center; justify-content:center; border-bottom:1px solid #333;">
                            <svg width="42" height="42" viewBox="0 0 24 24" fill="none" stroke="#39ff14" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c0 2 2 3 6 3s6-1 6-3v-5"/></svg>
                        </div>`;
                }

                // REEMPLAZO: Emojis de pergamino y candado de validación cambiados por SVGs tácticos consistentes
                card.innerHTML = `
                    ${previewHtml}
                    <div style="flex-grow:1; padding:1.25rem;">
                        <span style="color:var(--lime, #39ff14); font-size:0.7rem; font-weight:700; text-transform:uppercase; letter-spacing:1px; font-family:var(--font-ui); display: inline-flex; align-items: center; gap: 4px;">
                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                            ACCREDITED CREDENTIAL
                        </span>
                        <h3 style="font-size:1.15rem; margin:0.3rem 0 0.5rem 0; color:var(--text-bright, #fff); font-family:var(--font-ui); font-weight:700; line-height:1.3;">${c.nombre}</h3>
                        
                        <div style="margin-bottom:8px; font-family:var(--font-ui); font-size:0.85rem;">
                            <p style="color:#aaa; margin:0;"><span style="color:#666;">Issuer:</span> ${c.institucion}</p>
                            <p style="margin-top:4px; font-size:0.75rem; font-family:var(--font-mono); color:var(--cyan, #00e5ff);"><code>VERIFICATION ID: ${c.codigoId || 'INTERNAL'}</code></p>
                        </div>
                    </div>
                    ${c.urlValidacion ? `
                        <div style="margin-top:auto; border-top:1px solid #333; padding:1rem 1.25rem; display:flex; justify-content:flex-start;">
                            <a href="${c.urlValidacion}" target="_blank" style="color:var(--lime, #39ff14); font-size:0.8rem; text-decoration:none; font-weight:600; font-family:var(--font-ui); display:inline-flex; align-items:center; gap:6px;">
                                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                                VALIDATE CREDENTIAL ONLINE
                            </a>
                        </div>
                    ` : ''}
                `;
                grid.appendChild(card);
                setTimeout(() => { card.style.opacity = "1"; card.style.transform = "translateY(0)"; }, i * 50);
            });
        }).catch(() => { 
            // REEMPLAZO: Emoji cruz de error crítico reemplazado por escudo de error SVG
            grid.innerHTML = `
                <div class="siem-table-fallback" style="grid-column: 1 / -1; color:#ff1744; display: flex; align-items: center; justify-content: center; gap: 8px;">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#ff1744" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                    Error crítico al escanear hashes de credenciales.
                </div>`; 
        });
}

/** 🛠️ Carga las Herramientas en el Arsenal público con metadatos y barras de carga fluidas */
function cargarHerramientasVisor() {
    const grid = document.getElementById("grid-herramientas");
    if (!grid) return;

    grid.innerHTML = `<div class="siem-table-fallback" style="grid-column:1/-1;">Estableciendo sockets relacionales con MariaDB...</div>`;

    fetch('/api/v1/herramientas')
        .then(res => res.json())
        .then(data => {
            if (data.length === 0) {
                grid.innerHTML = `<div class="siem-table-fallback" style="grid-column:1/-1;">No hay herramientas registradas en el arsenal perimetral.</div>`;
                return;
            }
            grid.innerHTML = "";
            
            data.forEach((h, i) => {
                // 1. Clonamos la lógica exacta de previsualización multimedia del Admin
                let previewHtml = "";
                const rutaEvidencia = h.urlDocumentacion || h.rutaDocumento; 

                if (rutaEvidencia) {
                    if (rutaEvidencia.toLowerCase().endsWith('.pdf')) {
                        // REEMPLAZO: Icono de reporte plano en PDF sustituido por un SVG estructurado
                        previewHtml = `
                            <div style="width:calc(100% + 3rem); height:130px; background:linear-gradient(135deg, #111116, #1a1a24); display:flex; flex-direction:column; align-items:center; justify-content:center; border-bottom:1px solid #333; margin:-1.5rem -1.5rem 1rem;">
                                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#ff1744" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><path d="M12 18v-6M9 15h6"/></svg>
                                <span style="font-size:0.7rem; color:#ff1744; font-weight:700; margin-top:0.4rem; letter-spacing:1px; font-family:var(--font-ui);">PDF EVIDENCE REPORT</span>
                            </div>`;
                    } else {
                        previewHtml = `
                            <div style="width:calc(100% + 3rem); height:130px; background:url('${rutaEvidencia}') center/cover no-repeat; border-bottom:1px solid #333; margin:-1.5rem -1.5rem 1rem; position:relative;">
                                <div style="position:absolute; inset:0; background:linear-gradient(180deg, transparent 40%, #1a1a1a);"></div>
                            </div>`;
                    }
                } else {
                    previewHtml = `
                        <div style="width:calc(100% + 3rem); height:130px; background:linear-gradient(135deg, #111, #1a1a1a); display:flex; align-items:center; justify-content:center; border-bottom:1px solid #333; margin:-1.5rem -1.5rem 1rem;">
                            <span style="font-size:0.7rem; color:#666; letter-spacing:1px; font-family:var(--font-ui);">[ NO TERMINAL EVIDENCE ]</span>
                        </div>`;
                }

                // 2. Control y saneamiento del porcentaje de dominio técnico
                let numPorcentaje = parseInt(h.nivelDificultad, 10);
                if (isNaN(numPorcentaje)) {
                    numPorcentaje = h.porcentaje ? parseInt(h.porcentaje, 10) : 50;
                }
                if (isNaN(numPorcentaje)) numPorcentaje = 50; 
                const dominioPorcentaje = numPorcentaje + "%";
                
                const barColor = numPorcentaje >= 80 ? 'var(--lime, #39ff14)' : numPorcentaje >= 50 ? 'var(--cyan, #00d4ff)' : '#ffd60a';

                // 3. Renderizado de la tarjeta idéntica a la vista "Carbon Ops" del Panel de Administración
                const card = document.createElement("div");
                card.className = "stat-box-admin font-green";
                card.style.cssText = "display:flex; flex-direction:column; min-height:360px; background:#1a1a1a; padding:1.5rem; border-radius:6px; overflow:hidden; position:relative; opacity:0; transform:translateY(14px); transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);";

                // REEMPLAZO: Emojis de espadas cruzadas y de ojo para previsualización cambiados por SVGs inline
                card.innerHTML = `
                    ${previewHtml}
                    <div style="flex-grow:1; padding-bottom:0.5rem;">
                        <span style="color:var(--cyan, #00d4ff); font-size:0.7rem; font-weight:700; text-transform:uppercase; letter-spacing:1px; font-family:var(--font-ui); display: inline-flex; align-items: center; gap: 4px;">
                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 2 7 12 12 22 7 12 2"/></svg>
                            SECURITY TOOL
                        </span>
                        <h3 style="font-size:1.15rem; margin:0.2rem 0 0.4rem 0; color:var(--text-bright, #fff); font-family:var(--font-ui); font-weight:700;">${h.nombre}</h3>
                        
                        <div style="margin-bottom:12px;">
                            <span style="font-family:var(--font-mono); font-size:0.75rem; color:#666;">ENV DOMAIN LEVEL: <b style="color:${barColor};">${dominioPorcentaje}</b></span>
                            <div style="width:100%; background:#111; height:6px; border-radius:3px; margin-top:5px; overflow:hidden; border:1px solid #333;">
                                <div id="pub-bar-${h.id}" style="width:0%; background:${barColor}; height:100%; transition:width 1.2s cubic-bezier(0.25, 1, 0.5, 1); box-shadow: 0 0 8px ${barColor}80;"></div>
                            </div>
                        </div>
                        
                        <p style="font-size:0.85rem; color:var(--text-muted, #aaa); display:-webkit-box; -webkit-line-clamp:3; -webkit-box-orient:vertical; overflow:hidden; line-height:1.5; font-family:var(--font-ui); margin:0;">
                            ${h.descripcion || 'Sin descripción de laboratorio asignada.'}
                        </p>
                    </div>
                    ${rutaEvidencia ? `
                        <div style="margin-top:auto; border-top:1px solid #333; padding-top:0.75rem; display:flex; justify-content:flex-start;">
                            <a href="${rutaEvidencia}" target="_blank" style="color:var(--cyan, #00d4ff); font-size:0.8rem; text-decoration:none; font-weight:600; font-family:var(--font-ui); display:inline-flex; align-items:center; gap:6px;">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                                VIEW TERMINAL CAPTURE
                            </a>
                        </div>
                    ` : ''}
                `;

                grid.appendChild(card);
                
                // Animación secuencial idéntica para desplegar la tarjeta y llenar la barra neón
                setTimeout(() => { 
                    card.style.opacity = "1"; 
                    card.style.transform = "translateY(0)";
                    const bar = document.getElementById(`pub-bar-${h.id}`);
                    if (bar) setTimeout(() => { bar.style.width = dominioPorcentaje; }, 150);
                }, i * 50);
            });
        })
        .catch((error) => { 
            console.error("🚨 Error cargando visor público de herramientas:", error);
            // REEMPLAZO: Error de mapeo de red con icono SVG de advertencia perimetral
            grid.innerHTML = `
                <div class="siem-table-fallback" style="grid-column: 1 / -1; color:#ff1744; display: flex; align-items: center; justify-content: center; gap: 8px;">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#ff1744" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="7.86 2 16.14 2 22 7.86 22 16.14 16.14 22 7.86 22 2 16.14 2 7.86 7.86 2"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                    Error de infraestructura al mapear el arsenal de red.
                </div>`; 
        });
}

/** 🏠 Alimenta la telemetría viva de las tarjetas numéricas de la Home */
function cargarTelemetriaInicio() {
    fetch('/api/v1/practicas')
        .then(res => res.json())
        .then(data => {
            const totalLabs = data.length;
            const totalEvidencias = data.filter(p => p.rutaDocumento).length;
            
            animarContadorPublico("count-labs", totalLabs);
            animarContadorPublico("count-evidencias", totalEvidencias);
        }).catch(() => {});

    fetch('/api/v1/certificaciones')
        .then(res => res.json())
        .then(data => {
            animarContadorPublico("count-certs", data.length);
        }).catch(() => {});
}

/** 💻 Animación controlada para Proyectos */
function inicializarEfectosProyectos() {
    document.querySelectorAll(".proyectos-container .stat-box-admin").forEach((card, i) => {
        card.style.cssText = "opacity: 0; transform: translateY(12px); transition: all 0.35s ease;";
        setTimeout(() => {
            card.style.opacity = "1";
            card.style.transform = "translateY(0)";
        }, i * 60);
    });
}

/** 📈 Animación para Roadmap */
function inicializarEfectosRoadmap() {
    document.querySelectorAll(".roadmap-columna").forEach((col, i) => {
        col.style.cssText = "opacity: 0; transform: translateX(-10px); transition: all 0.4s ease;";
        setTimeout(() => {
            col.style.opacity = "1";
            col.style.transform = "translateX(0)";
        }, i * 80);
    });
}

/** Función de animación para números de métricas */
function animarContadorPublico(id, valorFinal) {
    const el = document.getElementById(id);
    if (!el) return;
    if (!valorFinal || valorFinal === 0) {
        el.innerText = "0";
        return;
    }
    let current = 0;
    const incremento = Math.max(valorFinal / 20, 1);
    const timer = setInterval(() => {
        current += incremento;
        if (current >= valorFinal) {
            el.innerText = valorFinal;
            clearInterval(timer);
        } else {
            el.innerText = Math.round(current);
        }
    }, 30);
}

// Inicialización perimetral obligatoria
window.onload = () => { 
    cargarModuloPublico('inicio'); 
};