/**
 * portfolio-core.js - Motor SPA y Controlador de Eventos Separado
 */

document.addEventListener("DOMContentLoaded", () => {
    cargarModuloPublico('inicio');
});

function cargarModuloPublico(nombreModulo) {
    fetch(`/html/modulos-publicos/${nombreModulo}.html`)
    .then(response => {
        if (!response.ok) throw new Error("Error cargando el viewport.");
        return response.text();
    })
    .then(html => {
        document.getElementById("main-content").innerHTML = html;
        
        // Disparador de lógica exclusivo según el módulo cargado
        inicializarLogicaModulo(nombreModulo);
    })
    .catch(err => console.error(err));
}

/**
 * ORQUESTADOR DE LOGICA DE CONTENIDO PURE JS
 */
function inicializarLogicaModulo(modulo) {
    if (modulo === 'inicio') {
        // Ejecuta la carga asíncrona de los contadores en inicio.html
        fetch('/api/v1/salud/metricas-globales') // Endpoint ficticio o real de conteo
            .then(res => res.json())
            .then(data => {
                document.getElementById("count-practicas").innerText = data.totalPracticas || 12;
                document.getElementById("count-labs").innerText = data.totalLabs || 8;
                document.getElementById("count-certs").innerText = data.totalCerts || 5;
            });
    } 
    
    else if (modulo === 'practicas') {
        const grid = document.getElementById("grid-practicas");
        
        fetch('/api/v1/pilares') 
            .then(res => res.json())
            .then(data => {
                if(data && data.length > 0) {
                    grid.innerHTML = ""; 
                    data.forEach(pilar => {
                        pilar.comandos.forEach(cmd => {
                            grid.innerHTML += generarHtmlTarjetaPractica(pilar.nombrePilar, cmd);
                        });
                    });
                } else {
                    grid.innerHTML = "<p>No hay auditorías registradas en la base de datos por el administrador.</p>";
                }
            })
            .catch(() => {
                grid.innerHTML = "<p class='error'>Error al conectar con el API Rest del SIEM.</p>";
            });
    }
}

/**
 * FUNCIÓN AUXILIAR DE RENDERIZADO (Mantiene el HTML del módulo limpio)
 */
function generarHtmlTarjetaPractica(nombrePilar, cmd) {
    return `
        <div class="practica-card">
            <div class="card-badge">${nombrePilar}</div>
            <h3>Análisis Técnico de Seguridad</h3>
            <div class="card-meta">
                <span>🛠️ <strong>Herramienta:</strong> ${cmd.herramienta.nombre}</span>
            </div>
            <div class="console-box">
                <code>$ ${cmd.sintaxis}</code>
            </div>
            <div class="card-details">
                <p><strong>Vulnerabilidad:</strong> ${cmd.vulnerabilidadAsociada}</p>
                <div class="mitigation-box">
                    <strong> Mitigación:</strong> ${cmd.mitigacion}
                </div>
            </div>
        </div>`;
}

// Las funciones preguntarIA() y evaluarEnter() se quedan abajo intactas...

/**
 * Extensión del orquestador dentro de portfolio-core.js
 */
function inicializarLogicaModulo(modulo) {
    // ... (Mantienes tus bloques de 'inicio' y 'practicas' intactos) ...

    if (modulo === 'laboratorios') {
        const gridLabs = document.getElementById("grid-laboratorios");
        
        // Consumimos el endpoint público de entornos/laboratorios
        fetch('/api/v1/entornos') 
            .then(res => res.json())
            .then(data => {
                if (data && data.length > 0) {
                    gridLabs.innerHTML = "";
                    data.forEach(lab => {
                        gridLabs.innerHTML += generarHtmlTarjetaLaboratorio(lab);
                    });
                } else {
                    gridLabs.innerHTML = "<p>No hay laboratorios registrados en esta auditoría.</p>";
                }
            })
            .catch(() => {
                gridLabs.innerHTML = "<p class='error'>Error al conectar con el inventario de entornos.</p>";
            });
    }
    
    else if (modulo === 'certificaciones') {
        const gridCerts = document.getElementById("grid-certificaciones");
        
        // Consumimos el endpoint público de tus certificaciones
        fetch('/api/v1/certificaciones') 
            .then(res => res.json())
            .then(data => {
                if (data && data.length > 0) {
                    gridCerts.innerHTML = "";
                    data.forEach(cert => {
                        gridCerts.innerHTML += generarHtmlTarjetaCertificacion(cert);
                    });
                } else {
                    gridCerts.innerHTML = "<p>No se han cargado credenciales en este bloque.</p>";
                }
            })
            .catch(() => {
                gridCerts.innerHTML = "<p class='error'>Error al verificar el almacén de credenciales.</p>";
            });
    }
}

/**
 * FUNCIONES AUXILIARES DE MAQUETACIÓN (Renders aislados del HTML)
 */
function generarHtmlTarjetaLaboratorio(lab) {
    return `
        <div class="lab-card">
            <div class="lab-platform-badge">${lab.plataforma || 'Local Lab'}</div>
            <h3>${lab.nombreEntorno}</h3>
            <p>${lab.descripcion || 'Entorno de pruebas y hardening de servicios.'}</p>
            <div class="lab-meta">
                <span>🎯 Nivel/Estado: <strong>${lab.estado || 'Completado'}</strong></span>
            </div>
        </div>`;
}

function generarHtmlTarjetaCertificacion(cert) {
    return `
        <div class="cert-card">
            <div class="cert-icon">📜</div>
            <h3>${cert.nombre}</h3>
            <p><strong>Emisor:</strong> ${cert.institucion}</p>
            <div class="cert-meta">
                <span>📅 Emitido: ${cert.fecha || '2025'}</span>
                <span>id: <code>${cert.codigoCredencial || 'N/A'}</code></span>
            </div>
            ${cert.urlValidacion ? `<a href="${cert.urlValidacion}" target="_blank" class="btn-verify">Verificar Credencial</a>` : ''}
        </div>`;
}

/**
 * Inyecciones finales en la función orquestadora inicializarLogicaModulo
 */
function inicializarLogicaModulo(modulo) {
    // ... (Módulos anteriores: inicio, practicas, laboratorios, certificaciones) ...

    if (modulo === 'herramientas') {
        const gridTools = document.getElementById("grid-herramientas");
        
        fetch('/api/v1/herramientas') // Consumo de tus herramientas del backend
            .then(res => res.json())
            .then(data => {
                gridTools.innerHTML = "";
                // Mapeo dinámico con barras de porcentaje o progreso
                data.forEach(tool => {
                    gridTools.innerHTML += `
                        <div class="skill-card">
                            <div class="skill-info">
                                <strong>${tool.nombre}</strong>
                                <span>${tool.porcentaje || '80'}%</span>
                            </div>
                            <div class="progress-bar-bg">
                                <div class="progress-bar-fill" style="width: ${tool.porcentaje || '80'}%"></div>
                            </div>
                        </div>`;
                });
            })
            .catch(() => gridTools.innerHTML = "<p class='error'>Error al leer el arsenal técnico.</p>");
    }

    else if (modulo === 'proyectos') {
        const gridProy = document.getElementById("grid-proyectos");
        
        fetch('/api/v1/proyectos') // Endpoint de tus desarrollos
            .then(res => res.json())
            .then(data => {
                if(data && data.length > 0) {
                    gridProy.innerHTML = "";
                    data.forEach(proy => {
                        gridProy.innerHTML += `
                            <div class="project-card">
                                <h3>${proy.nombre}</h3>
                                <p>${proy.descripcion}</p>
                                <div class="project-tags"><code>${proy.tecnologias || 'Spring Boot | Docker'}</code></div>
                                <a href="${proy.urlGithub || '#'}" target="_blank" class="btn-git">Ver Código Fuente</a>
                            </div>`;
                    });
                } else {
                    gridProy.innerHTML = "<p>No hay proyectos registrados.</p>";
                }
            })
            .catch(() => gridProy.innerHTML = "<p class='error'>Error al cargar portafolio de proyectos.</p>");
    }

    else if (modulo === 'roadmap') {
        // Poblado rápido de metas estructuradas
        document.getElementById("roadmap-completado").innerHTML = `
            <div class="roadmap-item">✓ Core de Spring Boot & Java 17</div>
            <div class="roadmap-item">✓ Arquitectura de Redes y Routing Linux</div>`;
        document.getElementById("roadmap-progreso").innerHTML = `
            <div class="roadmap-item">⚡ Integración RAG con LLMs (Gemini)</div>
            <div class="roadmap-item">⚡ Hardening de APIs e Interceptores SIEM</div>`;
        document.getElementById("roadmap-futuro").innerHTML = `
            <div class="roadmap-item">🎯 Certificación CompTIA Security+ o Junior Pentester</div>
            <div class="roadmap-item">🎯 Kubernetes para Despliegues de Ciberdefensa</div>`;
    }
}