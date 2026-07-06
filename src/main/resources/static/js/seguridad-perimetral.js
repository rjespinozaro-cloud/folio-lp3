/**
 * CYBER-PORTFOLIO :: SUBSYSTEM -> NETWORK SECURITY & FORENSICS (FIXED REGEX & TOGGLE)
 */

// 💾 Persistencia de estado global segura fuera del ciclo de reinicialización del módulo
if (window.siemSoloPuertosAbiertos === undefined) {
    window.siemSoloPuertosAbiertos = true; 
}

async function inicializarModuloSeguridad(token) {
    const API_BASE = '/api/v1/admin/network';
    const headers = { 
        "Authorization": "Bearer " + token,
        "Content-Type": "application/json"
    };

    if (window.cyberSineAnimationId) {
        cancelAnimationFrame(window.cyberSineAnimationId);
    }

    let nivelActividadRed = 15; 

    // ── 1. MOTOR GRÁFICO (OSCILOSCOPIO RADAR) ──────────────────────────────────
    function iniciarOsciloscopio() {
        const canvas = document.getElementById("siem-sine-canvas");
        if (!canvas) return;
        const ctx = canvas.getContext("2d");

        canvas.width = canvas.clientWidth;
        canvas.height = canvas.clientHeight;
        let trackingOffset = 0;

        function dibujarOnda() {
            if (!document.getElementById("siem-sine-canvas")) return; 

            ctx.clearRect(0, 0, canvas.width, canvas.height);
            
            ctx.strokeStyle = "rgba(0, 255, 200, 0.04)";
            ctx.lineWidth = 1;
            for (let i = 0; i < canvas.width; i += 30) {
                ctx.beginPath(); ctx.moveTo(i, 0); ctx.lineTo(i, canvas.height); ctx.stroke();
            }
            for (let j = 0; j < canvas.height; j += 25) {
                ctx.beginPath(); ctx.moveTo(0, j); ctx.lineTo(canvas.width, j); ctx.stroke();
            }

            ctx.strokeStyle = "rgba(0, 255, 200, 0.1)";
            ctx.beginPath(); ctx.moveTo(0, canvas.height / 2); ctx.lineTo(canvas.width, canvas.height / 2); ctx.stroke();

            ctx.strokeStyle = "#00ffcc";
            ctx.shadowBlur = 10; ctx.shadowColor = "#00ffcc"; ctx.lineWidth = 2; ctx.beginPath();

            for (let x = 0; x < canvas.width; x++) {
                let y = canvas.height / 2 + Math.sin(x * 0.015 + trackingOffset) * nivelActividadRed;
                if (x === 0) ctx.moveTo(x, y); else ctx.lineTo(x, y);
            }
            ctx.stroke(); ctx.shadowBlur = 0;

            trackingOffset -= 0.05; 
            window.cyberSineAnimationId = requestAnimationFrame(dibujarOnda);
        }
        dibujarOnda();
    }

    // ── 2. CARGAR, DE-DUPLICAR Y FILTRAR PUERTOS EN VIVO ───────────────────────
 // ── 2. CARGAR, DETECTAR PROTOCOLOS (TCP/UDP) Y FILTRAR PUERTOS EN VIVO ──
    async function cargarPuertos() {
        const consola = document.getElementById("siem-ports-terminal");
        if (!consola) return;
        try {
            const res = await fetch(`${API_BASE}/system-ports`, { headers });
            const datos = await res.json();
            if (datos.error) { consola.innerHTML = "<div class='sec-error'>⚠️ Permisos denegados a nivel Kernel.</div>"; return; }
            
            if (!datos.length) {
                consola.innerHTML = "<div class='sec-empty'>0 sockets detectados.</div>";
                return;
            }

            const puertosProcesados = new Set();
            let listaSocketsMapeados = [];

            datos.forEach(p => {
                const raw = p.raw_data;
                
                // Limpieza drástica de cabeceras de texto del sistema operativo
                if (raw.toLowerCase().includes("local address") || raw.toLowerCase().includes("peer address") || raw.toLowerCase().includes("state") || raw.trim() === "") {
                    return; 
                }

                let puerto = null;
                let estado = "OFF";
                // Identificar el protocolo por inspección de la cadena
                let protocolo = raw.toLowerCase().includes("udp") || raw.startsWith("udp") ? "UDP" : "TCP";

                // Regex avanzada que extrae el puerto sin importar el formato del sistema operativo
                const matchPuerto = raw.match(/:(\d+)(?:\s+|$|[^0-9])/);
                if (matchPuerto && matchPuerto[1]) {
                    puerto = matchPuerto[1];
                }

                if (!puerto) return;

                const portNum = parseInt(puerto);
                if (portNum <= 0 || portNum > 65535) return;

                // En UDP no existe el estado "LISTEN" formal, si el puerto está mapeado es porque está abierto (ON)
                if (protocolo === "UDP" || raw.toLowerCase().includes("listen") || raw.toLowerCase().includes("escuchando") || raw.toLowerCase().includes("establecido") || raw.toLowerCase().includes("estab")) {
                    estado = "ON";
                }

                // Generamos una llave única combinando Protocolo + Puerto para que no se pisen (ej: TCP-8080 y UDP-8080)
                const socketKey = `${protocolo}-${puerto}`;

                if (!puertosProcesados.has(socketKey)) {
                    puertosProcesados.add(socketKey);
                    listaSocketsMapeados.push({ puerto, estado, protocolo });
                }
            });

            // Ordenar numéricamente por puerto
            listaSocketsMapeados.sort((a, b) => parseInt(a.puerto) - parseInt(b.puerto));

            // Aplicar el interruptor del operador (Solo Abiertos vs Todo)
            if (window.siemSoloPuertosAbiertos) {
                listaSocketsMapeados = listaSocketsMapeados.filter(s => s.estado === "ON");
            }

            if (!listaSocketsMapeados.length) {
                consola.innerHTML = "<div style='color:#555; font-style:italic; padding:10px; font-size:12px;'>0 puertos bajo el filtro seleccionado.</div>";
                return;
            }

            // Renderizar la lista ciberpunk con Badges dinámicos de protocolo
            consola.innerHTML = listaSocketsMapeados.map(s => `
                <div class="sec-port-row" style="display:flex; align-items:center; justify-content:space-between; padding:6px 10px; border-bottom:1px solid rgba(0,255,204,0.05); font-family:monospace;">
                    <div style="display:flex; align-items:center; gap:10px;">
                        <span style="font-size:10px; padding:1px 5px; border-radius:3px; font-weight:bold; ${s.protocolo === 'TCP' ? 'background:rgba(0,170,255,0.15); color:#00aaff;' : 'background:rgba(255,170,0,0.15); color:#ffaa00;'}">
                            ${s.protocolo}
                        </span>
                        <strong class="sec-ip-highlight" style="font-size:14px;"># ${s.puerto}</strong>
                    </div>
                    <span style="padding:2px 8px; font-weight:bold; border-radius:3px; font-size:11px; ${s.estado === 'ON' ? 'background:rgba(0,255,204,0.12); color:#00ffcc;' : 'background:rgba(255,68,68,0.12); color:#ff4444;'}">
                        ● ${s.estado}
                    </span>
                </div>
            `).join('');

            const activos = listaSocketsMapeados.filter(s => s.estado === "ON").length;
            nivelActividadRed = activos > 8 ? 45 : 15;

        } catch (e) { console.error("Error en subcapa de sockets:", e); }
    }

    // ── Interfaz del Conmutador de Filtro (Corregida actualización de UI)
    window.toggleFiltroPuertos = () => {
        window.siemSoloPuertosAbiertos = !window.siemSoloPuertosAbiertos;
        const btn = document.getElementById("siem-toggle-filter-btn");
        if (btn) {
            btn.innerHTML = window.siemSoloPuertosAbiertos ? "MOSTRAR PUERTOS OFF" : "OCULTAR PUERTOS OFF";
            btn.style.borderColor = window.siemSoloPuertosAbiertos ? "#ffaa00" : "#00ffcc";
            btn.style.color = window.siemSoloPuertosAbiertos ? "#ffaa00" : "#00ffcc";
        }
        cargarPuertos(); 
    };

    // Sincronizar dinámicamente el texto visual del botón según la configuración actual al renderizar la vista
    const btnFiltroInicial = document.getElementById("siem-toggle-filter-btn");
    if (btnFiltroInicial) {
        btnFiltroInicial.innerHTML = window.siemSoloPuertosAbiertos ? "MOSTRAR PUERTOS OFF" : "OCULTAR PUERTOS OFF";
        btnFiltroInicial.style.borderColor = window.siemSoloPuertosAbiertos ? "#ffaa00" : "#00ffcc";
        btnFiltroInicial.style.color = window.siemSoloPuertosAbiertos ? "#ffaa00" : "#00ffcc";
    }

    // ── 3. CARGAR AUDITORÍA DE DISPOSITIVOS (CAMELCASE) ─────────────────────────
    async function cargarDispositivos() {
    const grid = document.getElementById("sessions-hardware-grid");
    const tablaHardware = document.getElementById("hardware-sessions-table-body");
    if (!grid || !tablaHardware) return;

    try {
        const res = await fetch(`${API_BASE}/active-sessions`, { headers });
        let datos = await res.json();
        if (!Array.isArray(datos)) datos = [];
        
        // ──> 1. INTERCEPCIÓN EN TIEMPO REAL DESDE EL FLUJO SIEM
        if (window.ipsActivasSiem && window.ipsActivasSiem.length) {
            window.ipsActivasSiem.forEach((ip, index) => {
                // Verificamos si la IP ya tiene un rol registrado como tráfico detectado
                const yaExisteVirtual = datos.some(d => (d.ipOrigen || d.ip_origen) === ip && d.dispositivo === "AGENT_DETECTED_TRAFFIC");
                
                if (!yaExisteVirtual) {
                    datos.push({
                        // Generamos un ID volátil único por iteración para que no colisionen
                        id: `siem-virtual-${index}-${Math.random().toString(36).substr(2, 5)}`,
                        dispositivo: "AGENT_DETECTED_TRAFFIC",
                        usuarioEmail: "Intercepción SIEM",
                        ipOrigen: ip,
                        lugar: "Análisis Perimetral",
                        tipoDispositivo: "NETWORK_NODE",
                        ultimaConexion: new Date().toISOString(),
                        activa: true
                    });
                }
            });
        }
        
        if (!datos.length) {
            grid.innerHTML = `<div class="sec-log-empty">✓ No se registran terminales concurrentes.</div>`;
            tablaHardware.innerHTML = `<tr><td colspan="5" class="sec-text-center">0 terminales mapeadas.</td></tr>`;
            return;
        }

        // ──> 2. FILTRO DE-DUPLICACIÓN AVANZADO (Laptop vs Celular en mismo Wi-Fi)
        const registrosUnicos = [];
        const llavesVistas = new Set();

        datos.forEach(dev => {
            const id = dev.id !== undefined ? dev.id : dev.session_id;
            const dispositivo = dev.dispositivo || dev.device_name || 'Terminal Desconocida';
            const ipOrigen = dev.ipOrigen || dev.ip_origen || '127.0.0.1';
            
            // 💡 SOLUCIÓN DEL ERROR: Combinamos ID + IP + Nombre.
            // Esto permite que convivan múltiples dispositivos de la misma red local sin pisarse.
            const llaveDispositivo = `${dispositivo}-${ipOrigen}-${id}`;

            if (!llavesVistas.has(llaveDispositivo)) {
                llavesVistas.add(llaveDispositivo);
                registrosUnicos.push({
                    id,
                    dispositivo,
                    usuarioEmail: dev.usuarioEmail || dev.usuario_email || 'Anónimo',
                    ipOrigen,
                    lugar: dev.lugar || dev.geo_location || 'Localhost',
                    tipoDispositivo: dev.tipoDispositivo || dev.device_type || 'DESKTOP',
                    ultimaConexion: dev.ultimaConexion || dev.last_login || dev.updated_at,
                    activa: dev.activa === true || dev.activa === 1 || dev.activa === "true"
                });
            }
        });

        // ──> 3. RENDERIZADO EN GRILLA CIBERPUNK
        grid.innerHTML = registrosUnicos.map(dev => `
            <div class="crypto-data-node sec-device-card" style="${dev.dispositivo === 'AGENT_DETECTED_TRAFFIC' ? 'border-left: 3px solid #ffaa00; background: rgba(255,170,0,0.02);' : ''}">
                <div class="sec-device-card-header">
                    <strong class="sec-device-name">💻 ${dev.dispositivo}</strong>
                    <span class="sec-badge-status ${dev.activa ? 'sec-badge-online' : 'sec-badge-offline'}">
                        ${dev.activa ? 'ONLINE' : 'TERMINATED'}
                    </span>
                </div>
                <div class="sec-device-meta">Operador: <span class="sec-text-highlight">${dev.usuarioEmail}</span></div>
                <div class="sec-device-footer">
                    <span class="sec-device-ip">IP: <span class="sec-ip-highlight">${dev.ipOrigen}</span></span>
                    <span class="sec-device-geo">${dev.lugar}</span>
                </div>
            </div>
        `).join('');

        // ──> 4. RENDERIZADO EN TABLA DE FORENSIA
        tablaHardware.innerHTML = registrosUnicos.map(dev => `
            <tr class="sec-blacklist-row">
                <td><strong>${dev.dispositivo}</strong><br><small class="sec-text-muted">IP: ${dev.ipOrigen}</small></td>
                <td><span class="sec-badge-role">${dev.tipoDispositivo}</span></td>
                <td>${dev.usuarioEmail}</td>
                <td>${dev.ultimaConexion ? new Date(dev.ultimaConexion).toLocaleString() : 'N/A'}</td>
                <td class="sec-action-cell" style="text-align: right;">
                    ${dev.activa ? `
                        <button onclick="expulsarDispositivo('${dev.id}')" class="sec-btn-action sec-btn-danger" style="background:transparent; border:1px solid #ff4444; color:#ff4444; padding:4px 8px; font-family:monospace; cursor:pointer;">
                            ${String(dev.id).includes('siem-virtual') ? 'BAN IP' : 'KICK / KILL'}
                        </button>
                    ` : `<span class="sec-text-muted" style="color:#555;">DISCONNECTED</span>`}
                </td>
            </tr>
        `).join('');

    } catch (e) { console.error("Error procesando hardware:", e); }
}

    // ── 4. CARGAR LISTA NEGRA CORTAFUEGOS (CAMELCASE) ───────────────────────────
    async function cargarListaNegra() {
        const tabla = document.getElementById("firewall-blacklist-table");
        if (!tabla) return;
        try {
            const res = await fetch(`${API_BASE}/blacklist`, { headers });
            const datos = await res.json();
            if (!datos.length) {
                tabla.innerHTML = `<tr class="sec-table-empty-row"><td colspan="5">✓ ESCUDO PERIMETRAL ACTIVO: Muro RAM limpio.</td></tr>`;
                return;
            }
            tabla.innerHTML = datos.map(item => `
                <tr class="sec-blacklist-row">
                    <td class="sec-ip-cell">⚡ ${item.ipBloqueada}</td>
                    <td class="sec-reason-cell">${item.motivoBloqueo}</td>
                    <td class="sec-date-cell">${item.fechaBloqueo ? new Date(item.fechaBloqueo).toLocaleString() : 'N/A'}</td>
                    <td class="sec-expire-cell">${item.expiracionBloqueo ? new Date(item.expiracionBloqueo).toLocaleTimeString() : 'PERMANENTE'}</td>
                    <td class="sec-action-cell" style="text-align: right;">
                        <button onclick="removerBan(${item.id})" class="sec-btn-action sec-btn-unban" style="background:transparent; border:1px solid #00ffcc; color:#00ffcc; padding:4px 8px; font-family:monospace; cursor:pointer;">
                            UNBAN
                        </button>
                    </td>
                </tr>
            `).join('');
        } catch (e) { console.error("Error en Firewall:", e); }
    }

    // ── 5. REGISTRO DE DISPARADORES GLOBALES (WINDOW) ─────────────────────────────
    window.removerBan = async (id) => {
        if (!confirm("¿Deseas revocar el aislamiento perimetral para esta IP?")) return;
        try {
            const res = await fetch(`${API_BASE}/blacklist/unban/${id}`, { method: 'DELETE', headers });
            if (res.ok) { cargarListaNegra(); }
        } catch (e) { alert("Fallo al purgar baneo."); }
    };

    window.solicitarBanManual = async () => {
        const ip = prompt("Establecer IP objetivo para aislamiento inmediato:");
        if (!ip) return;
        const motivo = prompt("Identificador del motivo de mitigación:", "Bloqueo preventivo administrativo");
        try {
            const res = await fetch(`${API_BASE}/blacklist/ban`, {
                method: 'POST',
                headers,
                body: JSON.stringify({ ip, motivo })
            });
            if (res.ok) { cargarListaNegra(); }
        } catch (e) { alert("Fallo al inyectar baneo."); }
    };

    window.expulsarDispositivo = async (id) => {
        if (!confirm("🚨 ¿Proceder con la terminación forzada (KICK/KILL) de la sesión de hardware?")) return;
        try {
            const res = await fetch(`${API_BASE}/active-sessions/${id}`, { method: 'DELETE', headers });
            if (res.ok) { cargarDispositivos(); }
        } catch (e) { alert("Fallo analítico al enviar señal Kill."); }
    };

    window.cargarPuertos = cargarPuertos;
    window.cargarDispositivos = cargarDispositivos;

    iniciarOsciloscopio();
    await Promise.all([cargarDispositivos(), cargarListaNegra(), cargarPuertos()]);
}