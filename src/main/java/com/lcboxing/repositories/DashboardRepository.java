package com.lcboxing.repositories;

import com.lcboxing.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DashboardRepository {
    private static final Logger logger = LoggerFactory.getLogger(DashboardRepository.class);

    /**
     * Obtiene estadísticas generales del dashboard
     */
    public Map<String, Object> getDashboardStats() throws SQLException {
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM atletas WHERE activo = TRUE) as atletas_activos, " +
                "(SELECT COUNT(*) FROM membresias WHERE id_estado_membresia = 1 AND fecha_vencimiento >= CURDATE()) as membresias_vigentes, " +
                "(SELECT COUNT(*) FROM asistencias WHERE fecha_asistencia = CURDATE()) as asistencias_hoy, " +
                "(SELECT COUNT(*) FROM asistencias WHERE fecha_asistencia = CURDATE() AND hora_salida IS NULL) as atletas_en_box, " +
                "(SELECT IFNULL(SUM(monto), 0) FROM pagos WHERE DATE(fecha_pago) = CURDATE()) as ingresos_hoy, " +
                "(SELECT IFNULL(SUM(monto), 0) FROM pagos WHERE MONTH(fecha_pago) = MONTH(CURDATE()) AND YEAR(fecha_pago) = YEAR(CURDATE())) as ingresos_mes, " +
                "(SELECT COUNT(*) FROM membresias WHERE id_estado_membresia = 1 AND fecha_vencimiento BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)) as vencimientos_proximos";

        Map<String, Object> stats = new HashMap<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                stats.put("atletasActivos", rs.getInt("atletas_activos"));
                stats.put("membresiasVigentes", rs.getInt("membresias_vigentes"));
                stats.put("asistenciasHoy", rs.getInt("asistencias_hoy"));
                stats.put("atletasEnBox", rs.getInt("atletas_en_box"));
                stats.put("ingresosHoy", rs.getBigDecimal("ingresos_hoy"));
                stats.put("ingresosMes", rs.getBigDecimal("ingresos_mes"));
                stats.put("vencimientosProximos", rs.getInt("vencimientos_proximos"));
            }

            return stats;

        } catch (SQLException e) {
            logger.error("Error al obtener estadísticas del dashboard", e);
            throw e;
        }
    }
}