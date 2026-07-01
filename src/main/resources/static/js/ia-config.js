/* ============================================================
   [E] IA CONFIGURATION :: CRUD TABLE (PREMIUM DESIGN)
   ============================================================ */

window.inicializarConfiguracionIA = function(token) {
    console.log("📡 Inicializando Pipeline CRUD para [IA-CONFIG]...");

    const formIa = document.getElementById("form-ia-config");
    const providerSelect = document.getElementById("ia-select-provider");
    const wrapperGemini  = document.getElementById("wrapper-gemini-key");
    const wrapperOllama  = document.getElementById("wrapper-ollama-url");

    if (!formIa) return;

    // 1. Forzar limpieza absoluta del formulario al cargar la pantalla por primera vez
    formIa.reset();
    if (document.getElementById("ia-id")) {
        document.getElementById("ia-id").value = "";
    }
    
    // Dejar los toggles visuales en su estado inicial por defecto (Gemini visible, Ollama oculto)
    if (wrapperGemini) wrapperGemini.style.display = "block";
    if (wrapperOllama) wrapperOllama.style.display = "none";

    // 2. Manejo exclusivo de visibilidad de credenciales según selección del usuario
    if (providerSelect) {
        providerSelect.addEventListener("change", () => {
            const isGemini = providerSelect.value === 'gemini';
            if (wrapperGemini) wrapperGemini.style.display = isGemini ? "block" : "none";
            if (wrapperOllama) wrapperOllama.style.display = isGemini ? "none" : "block";
        });
    }

    // 3. Renderizar contenido únicamente en la tabla de abajo (deja el formulario en blanco)
    listarTablaIA(token);

    // 4. Interceptar Submit del Formulario para Guardar o Actualizar
    formIa.onsubmit = function(e) {
        e.preventDefault();

        const idVal = document.getElementById("ia-id").value;
        const payload = {
            id: idVal ? parseInt(idVal) : null,
            proveedorActivo: providerSelect.value,
            nombreModelo:    document.getElementById("ia-model-name").value.trim(),
            geminiApiKey:    document.getElementById("ia-gemini-key").value.trim(),
            ollamaUrl:       document.getElementById("ia-ollama-url").value.trim(),
            systemPrompt:    document.getElementById("ia-system-prompt").value.trim()
        };

        fetch('/api/v1/configuracion-ia', {
            method: 'PUT',
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
        .then(res => {
            if (!res.ok) throw new Error();
            alert("✅ Configuración de IA guardada con éxito.");
            
            formIa.reset();
            document.getElementById("ia-id").value = "";
            if (providerSelect) providerSelect.dispatchEvent(new Event('change'));
            
            listarTablaIA(token);
        })
        .catch(() => alert("❌ Falló el guardado del motor IA. Verifique tipos en consola."));
    };
};

// Función encargada únicamente de pintar las filas de la tabla
function listarTablaIA(token) {
    const tableBody = document.getElementById("ia-table-body");
    if (!tableBody) return;

    fetch('/api/v1/configuracion-ia', {
        headers: { "Authorization": "Bearer " + token }
    })
    .then(res => {
        if (!res.ok) throw new Error();
        return res.json();
    })
    .then(data => {
        tableBody.innerHTML = "";
        
        const lista = Array.isArray(data) ? data : (data ? [data] : []);

        if (lista.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="3" style="text-align:center; color:#555; padding:20px;">No hay motores registrados.</td></tr>`;
            return;
        }

        lista.forEach(config => {
            const tr = document.createElement("tr");
            tr.style.borderBottom = "1px solid #1a1a1a";
            
            // Botones estilizados con diseño inline limpio para que combinen con tu entorno Crypto/Matrix
            tr.innerHTML = `
                <td style="padding: 12px 8px;"><span class="crypto-tag" style="background:#222; color:#00ffcc; padding:2px 6px; border-radius:4px; font-size:0.75rem;">${config.proveedorActivo.toUpperCase()}</span></td>
                <td style="padding: 12px 8px; color: #aaa; font-family: monospace;">${config.nombreModelo}</td>
                <td style="padding: 12px 8px; text-align: center; display: flex; gap: 8px; justify-content: center;">
                    
                    <button class="btn-editar-ia" style="background: rgba(0, 255, 204, 0.1); border: 1px solid #00ffcc; color: #00ffcc; padding: 5px 12px; border-radius: 4px; font-size: 0.8rem; font-weight: bold; cursor: pointer; transition: all 0.2s ease;">
                        Editar
                    </button>
                    
                    <button class="btn-eliminar-ia" style="background: rgba(255, 68, 68, 0.1); border: 1px solid #ff4444; color: #ff4444; padding: 5px 12px; border-radius: 4px; font-size: 0.8rem; font-weight: bold; cursor: pointer; transition: all 0.2s ease;">
                        Eliminar
                    </button>
                    
                </td>
            `;

            // Efectos visuales dinámicos (Hover) mediante JS para no depender de archivos CSS externos
            const btnEdit = tr.querySelector(".btn-editar-ia");
            const btnDel = tr.querySelector(".btn-eliminar-ia");

            btnEdit.onmouseenter = () => { btnEdit.style.background = "#00ffcc"; btnEdit.style.color = "#000"; };
            btnEdit.onmouseleave = () => { btnEdit.style.background = "rgba(0, 255, 204, 0.1)"; btnEdit.style.color = "#00ffcc"; };

            btnDel.onmouseenter = () => { btnDel.style.background = "#ff4444"; btnDel.style.color = "#000"; };
            btnDel.onmouseleave = () => { btnDel.style.background = "rgba(255, 68, 68, 0.1)"; btnDel.style.color = "#ff4444"; };

            // EVENTO EDITAR
            btnEdit.onclick = () => {
                document.getElementById("ia-id").value = config.id || "";
                document.getElementById("ia-select-provider").value = config.proveedorActivo;
                document.getElementById("ia-model-name").value = config.nombreModelo;
                document.getElementById("ia-gemini-key").value = config.geminiApiKey || "";
                document.getElementById("ia-ollama-url").value = config.ollamaUrl || "http://localhost:11434";
                document.getElementById("ia-system-prompt").value = config.systemPrompt;
                
                document.getElementById("ia-select-provider").dispatchEvent(new Event('change'));
            };

            // EVENTO ELIMINAR
            btnDel.onclick = () => {
                if (confirm(`¿Está seguro de eliminar el motor "${config.nombreModelo}"?`)) {
                    fetch(`/api/v1/configuracion-ia/${config.id}`, {
                        method: 'DELETE',
                        headers: { "Authorization": "Bearer " + token }
                    })
                    .then(res => {
                        if (!res.ok) throw new Error();
                        alert("🗑️ Motor eliminado correctamente.");
                        
                        const idActual = document.getElementById("ia-id").value;
                        if (idActual && parseInt(idActual) === config.id) {
                            document.getElementById("form-ia-config").reset();
                            document.getElementById("ia-id").value = "";
                            document.getElementById("ia-select-provider").dispatchEvent(new Event('change'));
                        }
                        
                        listarTablaIA(token);
                    })
                    .catch(() => alert("❌ Error al intentar eliminar el registro de la base de datos."));
                }
            };

            tableBody.appendChild(tr);
        });
    })
    .catch(err => console.error("❌ Error al renderizar el listado CRUD de la IA:", err));
}