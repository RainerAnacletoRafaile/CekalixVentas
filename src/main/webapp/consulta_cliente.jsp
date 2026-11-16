<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="modelo.Cliente" %>
<%
    if (request.getAttribute("clientes") == null) {
        response.sendRedirect("Proceso_Cliente");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CEKALIX - Consultar clientes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
   	<link href="css/style.css" rel="stylesheet">
</head>
<body>

<nav class="navbar navbar-dark navbar-cekalix px-3">
    <a class="navbar-brand fw-bold text-white" href="inicio.jsp">CEKALIX S.A.C.</a>
    <div>
        <a href="registro_cliente.jsp" class="btn btn-outline-light btn-sm me-2">Nuevo cliente</a>
        <a href="inicio.jsp" class="btn btn-outline-light btn-sm">Menú principal</a>
    </div>
</nav>

<div class="container py-4">

    <h4 class="mb-3">Consulta de clientes</h4>

    <% if (request.getParameter("actualizado") != null) { %>
        <div class="alert alert-success py-2">Cliente actualizado correctamente.</div>
    <% } else if (request.getParameter("error") != null) { %>
        <div class="alert alert-danger py-2">No se pudo actualizar el cliente (el RUC ya existe o hay datos inválidos).</div>
    <% } %>

    <!-- Buscador por RUC -->
    <form action="Proceso_Cliente" method="get" class="mb-4">
        <div class="input-group">
            <input type="text" name="ruc" class="form-control" maxlength="11"
                   placeholder="Buscar cliente por RUC">
            <button type="submit" class="btn btn-cekalix">Buscar</button>
            <a href="Proceso_Cliente" class="btn btn-outline-secondary">Ver todos</a>
        </div>
    </form>

    <!-- Tabla -->
    <div class="card shadow">
        <div class="table-responsive">
            <table class="table table-striped table-hover align-middle mb-0 tabla-cekalix">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Razón social</th>
                        <th>RUC</th>
                        <th>Teléfono</th>
                        <th class="text-center">Acción</th>
                    </tr>
                </thead>
                <tbody>

                <%
                    ArrayList<Cliente> clientes =
                        (ArrayList<Cliente>) request.getAttribute("clientes");

                    if (clientes != null && !clientes.isEmpty()) {
                        for (Cliente c : clientes) {
                %>
                    <tr>
                        <td><%= c.getId_cliente() %></td>
                        <td><%= c.getRazon_social() %></td>
                        <td><%= c.getRuc() %></td>
                        <td><%= c.getTelefono() == null ? "" : c.getTelefono() %></td>
                        <td class="text-center">
                            <button type="button" class="btn btn-sm btn-outline-danger"
                                    data-bs-toggle="modal"
                                    data-bs-target="#editar<%= c.getId_cliente() %>">
                                Editar
                            </button>
                        </td>
                    </tr>
                <%
                        }
                    } else {
                %>
                    <tr>
                        <td colspan="5" class="text-center text-muted py-4">No se encontraron clientes.</td>
                    </tr>
                <%
                    }
                %>

                </tbody>
            </table>
        </div>
    </div>

</div>

<%
    if (clientes != null) {
        for (Cliente c : clientes) {
%>
<div class="modal fade" id="editar<%= c.getId_cliente() %>" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">

            <form action="Proceso_Cliente" method="post">

                <div class="modal-header">
                    <h5 class="modal-title">Editar cliente</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>

                <div class="modal-body">
                    <input type="hidden" name="accion" value="actualizar">
                    <input type="hidden" name="id_cliente" value="<%= c.getId_cliente() %>">

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Razón social</label>
                        <input type="text" name="razon_social" class="form-control"
                               value="<%= c.getRazon_social() %>" required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">RUC</label>
                        <input type="text" name="ruc" class="form-control"
                               value="<%= c.getRuc() %>" maxlength="11" pattern="[0-9]{11}" required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Teléfono</label>
                        <input type="text" name="telefono" class="form-control"
                               value="<%= c.getTelefono() == null ? "" : c.getTelefono() %>" maxlength="15">
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-cekalix">Guardar cambios</button>
                </div>

            </form>

        </div>
    </div>
</div>
<%
        }
    }
%>

<!-- JavaScript de Bootstrap (necesario para el modal) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>