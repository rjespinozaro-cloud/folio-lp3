/**
 * CYBER-PORTFOLIO :: FRONTEND CONTROLLER -> ARSENAL & AUDITORÍA DE COMANDOS
 * v3.0 - DARK MODERN EDITION (Sincronizado con variables CSS del Panel)
 */

// Inicializar el nodo de edición global si no existe para evitar colisiones
if (!window.currentEditIds) {
    window.currentEditIds = { practica: null, certificacion: null, herramienta: null };
} else if (!window.currentEditIds.hasOwnProperty('herramienta')) {
    window.currentEditIds.herramienta = null;
}

let herramientasCache = []; 

/**
 * 1. LISTAR ASSETS (Renders en el mosaico dinámico de tu HTML con Previews de Terminal)
 */
async function listarHerramientasCrud(token) {
    const contenedorMosaico = document.getElementById("tool-mosaico-container");
    if (!contenedorMosaico) return;

    contenedorMosaico.innerHTML = `<div class="siem-table-fallback" style="grid-column:1/-1;">Estableciendo sockets relacionales con MariaDB...</div>`;

    try {
        const response = await fetch('/api/v1/herramientas', {
            method: 'GET',
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        });
        
        if (!response.ok) throw new Error("Fallo en el enlace con el repositorio central.");
        
        herramientasCache = await response.json();
        contenedorMosaico.innerHTML = ""; 

        if (herramientasCache.length === 0) {
            contenedorMosaico.innerHTML = '<div class="siem-table-fallback" style="grid-column: 1 / -1;">No hay herramientas registradas en el arsenal perimetral.</div>';
            return;
        }

        herramientasCache.forEach((h, i) => {
            const card = document.createElement("div");
            card.className = "stat-box-admin font-green";
            card.style.cssText = "display:flex; flex-direction:column; min-height:360px; overflow:hidden; position:relative; opacity:0; transform:translateY(14px);";

            let previewHtml = "";
            const rutaEvidencia = h.urlDocumentacion || h.rutaDocumento; 

            // Ajuste estético moderno de previsualizaciones (Mapeado a variables oscuras globales)
            if (rutaEvidencia) {
                if (rutaEvidencia.toLowerCase().endsWith('.pdf')) {
                    previewHtml = `
                        <div style="width:calc(100% + 3rem); height:130px; background:linear-gradient(135deg, var(--bg-root), var(--bg-card)); display:flex; flex-direction:column; align-items:center; justify-content:center; border-bottom:1px solid var(--border-dim); margin:-1.5rem -1.5rem 1rem;">
                            <span style="font-size:2rem;">📄</span>
                            <span style="font-size:0.7rem; color:var(--alert-danger); font-weight:700; margin-top:0.4rem; letter-spacing:1px; font-family:var(--font-ui);">PDF EVIDENCE REPORT</span>
                        </div>`;
                } else {
                    previewHtml = `
                        <div style="width:calc(100% + 3rem); height:130px; background:url('${rutaEvidencia}') center/cover no-repeat; border-bottom:1px solid var(--border-dim); margin:-1.5rem -1.5rem 1rem; position:relative;">
                            <div style="position:absolute; inset:0; background:linear-gradient(180deg, transparent 40%, var(--bg-surface));"></div>
                        </div>`;
                }
            } else {
                previewHtml = `
                    <div style="width:calc(100% + 3rem); height:130px; background:linear-gradient(135deg, var(--bg-root), var(--bg-surface)); display:flex; align-items:center; justify-content:center; border-bottom:1px solid var(--border-dim); margin:-1.5rem -1.5rem 1rem;">
                        <span style="font-size:0.7rem; color:var(--text-dim); letter-spacing:1px; font-family:var(--font-ui);">[ NO TERMINAL EVIDENCE ]</span>
                    </div>`;
            }

            let numPorcentaje = parseInt(h.nivelDificultad, 10);
            if (isNaN(numPorcentaje)) numPorcentaje = 50; 
            const dominioPorcentaje = numPorcentaje + "%";

            card.innerHTML = `
                ${previewHtml}
                <div style="flex-grow:1; padding-bottom:0.5rem;">
                    <span style="color:var(--cyan); font-size:0.7rem; font-weight:700; text-transform:uppercase; letter-spacing:1px; font-family:var(--font-ui);">⚔️ SECURITY TOOL</span>
                    <h3 style="font-size:1.1rem; margin:0.2rem 0 0.4rem; color:var(--text-bright); font-family:var(--font-ui); font-weight:700;">${h.nombre}</h3>
                    
                    <div style="margin-bottom:12px;">
                        <span style="font-family:var(--font-mono); font-size:0.75rem; color:var(--text-muted);">ENV DOMAIN LEVEL: <b style="color:var(--lime);">${dominioPorcentaje}</b></span>
                        <div style="width:100%; background:var(--bg-root); height:6px; border-radius:3px; margin-top:5px; overflow:hidden; border:1px solid var(--border-dim);">
                            <div style="width:${dominioPorcentaje}; background:var(--lime); height:100%; transition:width 0.4s ease;"></div>
                        </div>
                    </div>
                    
                    <p style="font-size:0.85rem; color:var(--text-muted); display:-webkit-box; -webkit-line-clamp:3; -webkit-box-orient:vertical; overflow:hidden; line-height:1.5; font-family:var(--font-ui);">${h.descripcion || 'Sin descripción de laboratorio asignada.'}</p>
                </div>
                <div style="margin-top:auto; display:flex; justify-content:space-between; align-items:center; border-top:1px solid var(--border-dim); padding-top:0.75rem; gap:8px;">
                    <div style="display:flex; gap:6px;">
                        ${rutaEvidencia ? `<a href="${rutaEvidencia}" target="_blank" style="color:var(--cyan); font-size:0.8rem; text-decoration:none; font-weight:600; font-family:var(--font-ui); align-self:center; margin-right:4px;">👁 VIEW CAP</a>` : ''}
                        <button class="btn-delete-action btn-edit-tool" style="background:rgba(59,130,246,0.12) !important; color:var(--cyan) !important; border-color:rgba(59,130,246,0.3) !important;">✏️ EDIT</button>
                    </div>
                    <button class="btn-delete-action btn-purge-tool">🗑 PURGE</button>
                </div>`;

            card.querySelector(".btn-edit-tool").addEventListener("click", () => {
                cargarDatosParaEditar(h.id);
            });

            card.querySelector(".btn-purge-tool").addEventListener("click", () => {
                eliminarHerramientaCrud(h.id, token);
            });

            contenedorMosaico.appendChild(card);
            setTimeout(() => {
                if (card) {
                    card.style.transition = "opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1), transform 0.3s cubic-bezier(0.4, 0, 0.2, 1)";
                    card.style.opacity = "1";
                    card.style.transform = "translateY(0)";
                }
            }, Math.min(i * 50, 1000));
        });

    } catch (error) {
        console.error("❌ Error listando herramientas:", error);
        contenedorMosaico.innerHTML = '<div class="siem-table-fallback" style="grid-column: 1 / -1; color:var(--alert-danger);">Error de infraestructura al conectar con el inventario.</div>';
    }
}

/**
 * 2. CARGA DINÁMICA DESDE MARIADB EN LOS SELECTS + INYECCIÓN DE BOTONES [ + ]
 */
async function cargarPilaresEnFormulario(token) {
    const selectPilar = document.getElementById("cmd-pilar-id");
    const selectEnfoque = document.getElementById("cmd-tipo");

    try {
        const response = await fetch('/api/v1/herramientas/opciones-formulario', {
            method: 'GET',
            headers: { 
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        });
        
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();

        if (selectPilar && data.pilares) {
            selectPilar.innerHTML = '<option value="">Seleccione Disciplina...</option>';
            data.pilares.forEach(pilar => {
                selectPilar.innerHTML += `<option value="${pilar.id}">${pilar.nombrePilar}</option>`;
            });
            inyectarBotonAgregarMetadato(selectPilar, "Agregar Disciplina", () => agregarNuevaDisciplinaEnCaliente(token));
        }

        if (selectEnfoque && data.enfoques) {
            selectEnfoque.innerHTML = '<option value="">Seleccione Enfoque...</option>';
            data.enfoques.forEach(enfoque => {
                const valor = (typeof enfoque === 'object') ? enfoque.tipoComando : enfoque;
                if (valor) {
                    selectEnfoque.innerHTML += `<option value="${valor}">${valor}</option>`;
                }
            });
            inyectarBotonAgregarMetadato(selectEnfoque, "Agregar Enfoque", () => agregarNuevoEnfoqueEnCaliente(token));
        }

    } catch (error) {
        console.error("❌ Error en la inyección de metadatos del formulario:", error);
    }
}

/**
 * UTILERÍA: INYECTAR BOTÓN [ + ] DINÁMICAMENTE AL LADO DEL SELECTOR (Adaptado al Dark Modern)
 */
function inyectarBotonAgregarMetadato(selectElement, tooltip, callbackAccion) {
    if (!selectElement) return;
    
    if (selectElement.parentNode.querySelector(".btn-add-inline-dic")) return;

    selectElement.parentNode.style.position = "relative";
    selectElement.style.paddingRight = "42px"; 

    const btn = document.createElement("button");
    btn.type = "button";
    btn.className = "btn-add-inline-dic";
    btn.innerText = "+";
    btn.title = tooltip;
    
    // Integrado con var(--lime) y var(--font-ui) para consistencia total
    btn.style.cssText = "position:absolute; right:8px; top:34px; background:rgba(163,230,53,0.1); border:1px solid var(--lime); color:var(--lime); width:28px; height:28px; border-radius:4px; font-weight:bold; cursor:pointer; font-size:15px; display:flex; align-items:center; justify-content:center; z-index:5; transition:all 0.2s ease; font-family:var(--font-ui);";

    btn.onmouseover = () => { btn.style.background = "var(--lime)"; btn.style.color = "var(--bg-root)"; };
    btn.onmouseout = () => { btn.style.background = "rgba(163,230,53,0.1)"; btn.style.color = "var(--lime)"; };
    btn.onclick = callbackAccion;

    selectElement.parentNode.appendChild(btn);
}

/**
 * ACCIÓN INLINE: AGREGAR NUEVA DISCIPLINA / PILAR (POST EN CALIENTE)
 */
async function agregarNuevaDisciplinaEnCaliente(token) {
    const nuevoNombre = prompt("⚔️ Ingrese el nombre de la nueva Disciplina / Pilar (Ej: POST-EXPLOTACIÓN, RECON):");
    if (!nuevoNombre || nuevoNombre.trim() === "") return;

    try {
        const response = await fetch('/api/v1/herramientas/pilares', {
            method: 'POST',
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ nombrePilar: nuevoNombre.trim().toUpperCase() })
        });

        if (!response.ok) throw new Error();
        alert("✅ Nueva disciplina indexada correctamente.");
        await cargarPilaresEnFormulario(token); 
    } catch (e) {
        alert("❌ Error de infraestructura al guardar la disciplina.");
    }
}

/**
 * ACCIÓN INLINE: AGREGAR NUEVO ENFOQUE OPERATIVO (POST EN CALIENTE)
 */
async function agregarNuevoEnfoqueEnCaliente(token) {
    const nuevoEnfoque = prompt("🎯 Ingrese el nombre del nuevo Enfoque Operativo (Ej: MANUAL, AUTOMATIZADO, STEALTH):");
    if (!nuevoEnfoque || nuevoEnfoque.trim() === "") return;

    try {
        const response = await fetch('/api/v1/herramientas/enfoques', {
            method: 'POST',
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ tipoComando: nuevoEnfoque.trim().toUpperCase() })
        });

        if (!response.ok) throw new Error();
        alert("✅ Nuevo enfoque operativo indexado correctamente.");
        await cargarPilaresEnFormulario(token); 
    } catch (e) {
        alert("❌ Error de infraestructura al guardar el enfoque.");
    }
}

/**
 * 3. COLOCAR DATOS EN EL FORMULARIO PARA INICIAR EDICIÓN (EDICIÓN EN CALIENTE)
 */
function cargarDatosParaEditar(id) {
    const asset = herramientasCache.find(h => h.id === id);
    if (!asset) return;

    window.currentEditIds.herramienta = asset.id; 
    const inputOculto = document.getElementById("tool-id-edicion");
    if (inputOculto) inputOculto.value = asset.id;
    
    document.getElementById("tool-nombre").value = asset.nombre;
    
    const dificultadPlana = asset.nivelDificultad ? asset.nivelDificultad.replace('%', '') : "";
    document.getElementById("tool-dificultad").value = dificultadPlana;
    
    document.getElementById("tool-descripcion").value = asset.descripcion || "";

    document.getElementById("form-herramientas").scrollIntoView({ behavior: 'smooth' });

    const btnSubmit = document.getElementById("btn-guardar-tool");
    if (btnSubmit) {
        btnSubmit.innerText = "UPDATE CYBER-ARSENAL // MODE";
        btnSubmit.style.background = "var(--magenta)"; // Cambio estético al color de resalte secundario moderno
        btnSubmit.style.borderColor = "var(--magenta)";
        btnSubmit.style.color = "var(--text-bright)";
    }
}

/**
 * 4. ENVIAR DATOS (CREAR NUEVA HERRAMIENTA CON POST O MODIFICARLA CON PUT)
 */
async function guardarHerramienta(token) {
    const form = document.getElementById("form-herramientas");
    const btn = document.getElementById("btn-guardar-tool");
    if (!form || !btn) return;

    const nombre = document.getElementById("tool-nombre").value.trim();
    const dificultad = document.getElementById("tool-dificultad").value.trim();

    if (!nombre || !dificultad) {
        alert("⚠️ El nombre del binario y el nivel de dominio son obligatorios.");
        return;
    }

    const formData = new FormData();
    formData.append("nombre", nombre);
    
    const porcentajeEntero = parseInt(dificultad.replace('%', ''), 10) || 50; 
    
    formData.append("porcentaje", porcentajeEntero); 
    formData.append("nivelDificultad", porcentajeEntero + "%"); 
    formData.append("descripcion", document.getElementById("tool-descripcion").value);

    const inputEvidencia = document.getElementById("tool-archivo-evidencia");
    if (inputEvidencia && inputEvidencia.files.length > 0) {
        formData.append("archivo", inputEvidencia.files[0]);
    }

    const sintaxisVal = document.getElementById("cmd-sintaxis") ? document.getElementById("cmd-sintaxis").value : "";
    if (sintaxisVal.trim() !== "") {
        formData.append("sintaxis", sintaxisVal);
        formData.append("pilarId", document.getElementById("cmd-pilar-id").value || "1");
        formData.append("tipoComando", document.getElementById("cmd-tipo").value || "");
        formData.append("vulnerabilidadAsociada", document.getElementById("cmd-vulnerabilidad").value || "");
        formData.append("mitigacion", document.getElementById("cmd-mitigacion").value || "");
        formData.append("descripcionPersonalizada", document.getElementById("cmd-descripcion").value || "");
        
        const subtemaVal = document.getElementById("cmd-subtema-id") ? document.getElementById("cmd-subtema-id").value : "";
        if (subtemaVal) formData.append("subtemaId", subtemaVal);
    }

    const idOcultoHtml = document.getElementById("tool-id-edicion") ? document.getElementById("tool-id-edicion").value : "";
    const idEdicion = idOcultoHtml || window.currentEditIds.herramienta;
    
    const url = idEdicion ? `/api/v1/herramientas/${idEdicion}` : '/api/v1/herramientas/con-archivo';
    const metodo = idEdicion ? 'PUT' : 'POST';

    btn.disabled = true;
    btn.innerText = idEdicion ? "UPDATING ASSET..." : "DEPLOYING ASSET...";

    try {
        const response = await fetch(url, {
            method: metodo,
            headers: { "Authorization": "Bearer " + token },
            body: formData
        });

        if (!response.ok) throw new Error("Rechazo transaccional en el backend.");

        alert(idEdicion ? "🔄 Ecosistema del asset actualizado con éxito." : "🚀 Activo indexado con éxito.");
        
        document.querySelectorAll(".btn-add-inline-dic").forEach(el => el.remove());

        form.reset();
        window.currentEditIds.herramienta = null;
        if (document.getElementById("tool-id-edicion")) {
            document.getElementById("tool-id-edicion").value = "";
        }
        
        btn.innerText = "DEPLOY TO CYBER-ARSENAL";
        btn.style.background = "";
        btn.style.borderColor = "";
        btn.style.color = "";

        await cargarPilaresEnFormulario(token);
        await listarHerramientasCrud(token);

    } catch (error) {
        console.error("🚨 Error crítico al guardar:", error);
        alert("Fallo de infraestructura perimetral al procesar la mutación del asset.");
    } finally {
        btn.disabled = false;
    }
}

/**
 * 5. ELIMINACIÓN DE HERRAMIENTAS (PURGE)
 */
async function eliminarHerramientaCrud(id, token) {
    if (!confirm("⚠️ ¿Confirmas la revocación destructiva de este registro?")) return;

    try {
        const response = await fetch(`/api/v1/herramientas/${id}`, {
            method: 'DELETE',
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        });

        if (response.status === 409) {
            alert("Operación cancelada: El asset contiene registros subordinados de comandos activos.");
            return;
        }
        
        if (!response.ok) throw new Error();

        alert("🧹 Asset revocado de los registros con éxito.");
        listarHerramientasCrud(token);

    } catch (error) {
        console.error("❌ Error en la revocación del recurso:", error);
        alert("Falló la comunicación con el endpoint de purga.");
    }
}