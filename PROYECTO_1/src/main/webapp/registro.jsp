<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CEKALIX - Registro</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
   	<link href="css/style.css" rel="stylesheet">
</head>
<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-11 col-sm-8 col-md-6 col-lg-4">

            <div class="card shadow mt-5">
                <div class="card-header marca text-center py-3">
                    <h3 class="mb-0 fw-bold">CEKALIX S.A.C.</h3>
                </div>

                <div class="card-body p-4">
                    <h5 class="text-center mb-3">Registro de usuario</h5>

                    <% if (request.getParameter("error") != null) { %>
                        <div class="alert alert-danger py-2">No se pudo registrar (el usuario ya existe).</div>
                    <% } %>

                    <form action="Proceso_Registro" method="post">
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Nombre completo</label>
                            <input type="text" name="nombre" class="form-control" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Usuario</label>
                            <input type="text" name="usuario" class="form-control" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-semibold">Contraseña</label>
                            <input type="password" name="contrasena" class="form-control" minlength="6" required>
                            <div class="form-text">Mínimo 6 caracteres.</div>
                        </div>

                        <button type="submit" class="btn btn-cekalix w-100">Registrarse</button>
                    </form>

                    <p class="text-center mt-3 mb-0">
                        ¿Ya tienes una cuenta? <a href="login.jsp">Iniciar sesión</a>
                    </p>
                </div>
            </div>

        </div>
    </div>
</div>

</body>
</html>