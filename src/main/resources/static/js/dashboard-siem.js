/**
 * CYBER-PORTFOLIO :: MODULE -> DASHBOARD SIEM (CADENA ULTRA-VELOZ 1s & DETALLE EXPANDE_ERROR)
 */

function calcularNivelAmenaza(status, tieneError) {
    if (status >= 500 || (status === 401 && tieneError)) {
        return { etiqueta: "CRITICAL", clase: "alert-critical" };
    }
    if (status >= 400) {
        return { etiqueta: "MEDIUM", clase: "alert-medium" };
    }
    return { etiqueta: "LOW", clase: "alert-low" };
}

async function cargarTelemetriaDashboard(token) {
    const tableBody  = document.getElementById("siem-table-body");
    const countLogs  = document.getElementById("admin-total-logs");
    const tokensCard = document.getElementById("admin-tokens-ia");

    try {
        const res = await fetch('/api/v1/admin/siem-logs', {
            headers: { "Authorization": "Bearer " + token }
        });
        if (!res.ok) throw new Error();
        const logs = await res.json();

        // 1. 🛡️ CONTROL ANTI-PARPADEO EN CONTADORES GLOBALES
        if (countLogs) {
            const nuevoTotal = logs.length;
            if (parseInt(countLogs.innerText) !== nuevoTotal) {
                animarContador(countLogs, nuevoTotal);
            }
        }
        
        if (tokensCard) {
            // 💡 NUEVO PROTOCOLO: Filtramos y contamos estrictamente por IPs de origen únicas
            // Esto permite que si un administrador usa Tailscale y Red Local en paralelo, marque "2"
            const ipsUnicas = new Set(
                logs
                    .map(l => l.ipOrigen)
                    .filter(ip => ip && ip.trim() !== "" && ip !== "unknown")
            );
            
            const totalEntornos = ipsUnicas.size || (logs.length ? 1 : 0);
            
            if (parseInt(tokensCard.innerText) !== totalEntornos) {
                animarContador(tokensCard, totalEntornos);
            }
        }

        if (!tableBody) return;

        // 2. FIJACIÓN DE PURGA INTEGRAL
        if (!logs.length) {
            tableBody.innerHTML = '<tr><td colspan="8" class="siem-table-fallback">No hay logs de red interceptados. Nivel de amenaza: CERO.</td></tr>';
            return;
        }

        if (tableBody.querySelector('.siem-table-fallback')) {
            tableBody.innerHTML = "";
        }

        // 3. SINCRONIZACIÓN INTELIGENTE DE CADENA (Elimina únicamente lo viejo)
        const listaIdsNuevos = logs.map(l => `log-${l.timestamp}-${l.endpoint.replace(/\//g, '-')}`);
        Array.from(tableBody.children).forEach(row => {
            if (row.id && !listaIdsNuevos.includes(row.id)) {
                row.remove(); 
            }
        });

        // 4. INYECCIÓN EN CASCADA (Efecto empuje Matrix/Flujo Continuo)
        logs.reverse().forEach((log) => {
            const logId = `log-${log.timestamp}-${log.endpoint.replace(/\//g, '-')}`;
            
            if (document.getElementById(logId)) return; 

            const tr = document.createElement("tr");
            tr.id = logId;
            tr.className = "siem-row-chain siem-row-stream"; 
            tr.innerHTML = generarFilaSiemLog(log);

            tableBody.insertBefore(tr, tableBody.firstChild);
        });

    } catch (err) {
        console.error("Error en la tubería SIEM:", err);
        if (tableBody && !tableBody.children.length) {
            tableBody.innerHTML = `<tr><td colspan="8" class="error-table">⚠ Error de enlace perimetral con los registros SIEM.</td></tr>`;
        }
    }
}

function generarFilaSiemLog(log) {
    const fecha = log.timestamp ? new Date(log.timestamp).toLocaleString() : new Date().toLocaleString();
    
    // 🔍 AMPLIACIÓN DE DETALLES: Subido de 30 a 85 caracteres + guardado completo para debugging
    let errorCorto = '<span class="success-tag-table">✓ clean</span>';
    if (log.detallesError) {
        const textoLimpio = log.detallesError.replace(/"/g, '&quot;');
        errorCorto = `
            <span class="error-tag-table" 
                  title="${textoLimpio}" 
                  data-error-raw="${textoLimpio}"
                  style="cursor:help; font-size:11px; font-family:monospace; color:#ff4444; word-break:break-all;"
                  onclick="console.dir(this.dataset.errorRaw)">
                ⚠️ ${log.detallesError.length > 85 ? log.detallesError.substring(0, 85) + '...' : log.detallesError}
            </span>`;
    }
    
    const status = log.statusHttp || 200;
    const statusClass = status >= 400 ? 'status-http-bad' : 'status-http-good';
    
    // Alerta calculada con icono SVG integrado
    const am = calcularNivelAmenaza(status, !!log.detallesError);
    const iconAlert = `<svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="display:inline-block;vertical-align:middle;margin-right:4px;"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>`;

    return `
        <td><span class="siem-badge-alert ${am.clase}">${iconAlert}${am.etiqueta}</span></td>
        <td><code>${fecha}</code></td>
        <td><span class="badge-method">${log.metodoHttp || 'GET'}</span></td>
        <td><code>${log.endpoint || '/'}</code></td>
        <td>${log.ipOrigen || '127.0.0.1'}</td>
        <td><strong class="${statusClass}">${status}</strong></td>
        <td>${log.usuarioEmail || '<span style="color:var(--text-secondary)">Anónimo</span>'}</td>
        <td>${errorCorto}</td>`;
}
/**
 * 🔁 MOTOR DE POLLING ULTRA-VELOZ (1s)
 * Se auto-pausa si la pestaña pierde foco (ahorra requests) y se reanuda al volver.
 */
let _siemIntervalId = null;

function iniciarStreamSiem(token) {
    if (_siemIntervalId) clearInterval(_siemIntervalId);

    cargarTelemetriaDashboard(token); // primera carga inmediata

    _siemIntervalId = setInterval(() => {
        cargarTelemetriaDashboard(token);
    }, 1000); // <-- cadena ultra-veloz: 1s exacto
}

function detenerStreamSiem() {
    if (_siemIntervalId) {
        clearInterval(_siemIntervalId);
        _siemIntervalId = null;
    }
}

document.addEventListener("visibilitychange", () => {
    const token = localStorage.getItem("token") || sessionStorage.getItem("token");
    if (document.hidden) {
        detenerStreamSiem();
    } else if (token) {
        iniciarStreamSiem(token);
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token") || sessionStorage.getItem("token");
    if (token) iniciarStreamSiem(token);
});