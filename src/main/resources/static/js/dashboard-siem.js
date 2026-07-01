/**
 * CYBER-PORTFOLIO :: MODULE -> DASHBOARD SIEM
 */

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

        // 1. Actualizar Contadores Globales
        if (countLogs) {
            animarContador(countLogs, logs.length);
        }
        if (tokensCard) {
            const total = logs.reduce((acc, l) => acc + (l.tokensConsumidos || 0), 0);
            animarContador(tokensCard, total);
        }

        if (!tableBody) return;

        // 2. 🔥 FIJACIÓN DE PURGA INTEGRAL: Si la BD viene vacía, limpia la UI inmediatamente
        if (!logs.length) {
            tableBody.innerHTML = '<tr><td colspan="7" class="siem-table-fallback">No hay logs de red interceptados. Nivel de amenaza: CERO.</td></tr>';
            return;
        }

        // 3. Control de contingencia para limpiar el mensaje de fallback si entran nuevos logs
        if (tableBody.querySelector('.siem-table-fallback')) {
            tableBody.innerHTML = "";
        }

        // 4. 🔥 SINCRONIZACIÓN INTELIGENTE: Eliminar del DOM los logs que ya no existen en la BD (Post-Purga)
        const listaIdsNuevos = logs.map(l => `log-${l.timestamp}-${l.endpoint.replace(/\//g, '-')}`);
        Array.from(tableBody.children).forEach(row => {
            if (row.id && !listaIdsNuevos.includes(row.id)) {
                row.remove(); // Remueve de pantalla lo que ya no está en la BD
            }
        });

        // 5. Inyección ordenada de registros nuevos
        logs.slice().reverse().forEach((log) => {
            const logId = `log-${log.timestamp}-${log.endpoint.replace(/\//g, '-')}`;
            if (document.getElementById(logId)) return; // Evita duplicar si ya está pintado

            const tr = document.createElement("tr");
            tr.id = logId;
            tr.className = "siem-row-chain"; 
            tr.innerHTML = generarFilaSiemLog(log);

            // Inserta al principio para ver la telemetría en tiempo real
            tableBody.insertBefore(tr, tableBody.firstChild);
        });

    } catch (err) {
        console.error("Error en la tubería SIEM:", err);
        if (tableBody && !tableBody.children.length) {
            tableBody.innerHTML = `<tr><td colspan="7" class="error-table">⚠ Error de enlace perimetral con los registros SIEM.</td></tr>`;
        }
    }
}
function generarFilaSiemLog(log) {
    const fecha = log.timestamp ? new Date(log.timestamp).toLocaleString() : new Date().toLocaleString();
    const errorCorto = log.detallesError
        ? `<span class="error-tag-table" title="${log.detallesError}">⚠️ ${log.detallesError.substring(0, 30)}...</span>`
        : '<span class="success-tag-table">✓ Clean</span>';
    
    const status = log.statusHttp || 200;
    const statusClass = status >= 400 ? 'status-http-bad' : 'status-http-good';

    return `
        <td><code>${fecha}</code></td>
        <td><span class="badge-method">${log.metodoHttp || 'GET'}</span></td>
        <td><code>${log.endpoint || '/'}</code></td>
        <td>${log.ipOrigen || '127.0.0.1'}</td>
        <td><strong class="${statusClass}">${status}</strong></td>
        <td>${log.usuarioEmail || '<span style="color:var(--text-secondary)">Anónimo</span>'}</td>
        <td>${errorCorto}</td>`;
}