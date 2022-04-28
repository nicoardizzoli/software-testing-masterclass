package com.nicoardizzoli.testing.payment.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * ESTA CLASE ES PARA ENCAPSULAR EL METODO STATIC Y PODER LUEGO ESTEARLO
 */
@Service
public class StripeApi {

    public Charge create(Map<String, Object> map, RequestOptions options) throws StripeException {
        return Charge.create(map, options);
    }

}
