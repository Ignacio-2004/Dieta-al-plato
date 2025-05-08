package com.tfg.dietaalplato.firebase.exceptions;

//++IP - 7/5/2025 -

import com.tfg.dietaalplato.firebase.utilities.ObjectResult;

/***
 * Excepcion hija de FBCException, cuyo objetivo es indicar el motivo de la excepcion con mucha mas claridad al permitir devolver un objeto como ObjectResult
 */

public class ComplexFBCE extends FBCException {

    public ObjectResult<?> reason;

    public ComplexFBCE(String message) {
        super(message);
    }
    public ComplexFBCE(ObjectResult<?> reason) {
        this.reason = reason;
    }
}

//--IP 7/5/2025
