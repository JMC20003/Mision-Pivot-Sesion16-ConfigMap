package com.nttdata.envios;

import com.nttdata.envios.model.CotizacionRequest;
import com.nttdata.envios.model.CotizacionResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Calcula el costo de un envio con la configuracion activa. */
@Service
public class CotizadorService {

    private final EnviosProperties props;

    public CotizadorService(EnviosProperties props) {
        this.props = props;
    }

    public CotizacionResponse cotizar(CotizacionRequest req) {
        // TODO 10 (RN-04): si el peso es <= 0 o mayor a props.pesoMaximoKg(),
        //                  lanza IllegalArgumentException.
        if (req.pesoKg() <= 0 || req.pesoKg() > props.pesoMaximoKg()) {
            throw new IllegalArgumentException("El peso debe ser mayor que 0 y no superar el maximo permitido.");
        }

        // TODO 13 (RN-03): si NO es express y la compra es >= props.envioGratisDesde(),
        //                  devuelve costo 0.00 con gratis = true.
        if (!req.express() && req.montoCompra() >= props.envioGratisDesde()) {
            return new CotizacionResponse(0.00, props.moneda(), true, false);
        }

        // TODO 11 (RN-01): costo = tarifa base + peso x costo por kg.
        double costo = props.tarifaBase() + req.pesoKg() * props.costoPorKg();

        // TODO 12 (RN-02): si es express, aplica el recargo: costo x (1 + recargoExpress).
        if (req.express()) {
            costo *= 1 + props.recargoExpress();
        }

        // TODO 14 (RN-05): devuelve la respuesta con el costo redondeado a 2 decimales
        //                  usando el metodo redondear(...).
        return new CotizacionResponse(redondear(costo), props.moneda(), false, req.express());
    }

    /** Redondeo a 2 decimales, HALF_UP. (Resuelto) */
    private static double redondear(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
