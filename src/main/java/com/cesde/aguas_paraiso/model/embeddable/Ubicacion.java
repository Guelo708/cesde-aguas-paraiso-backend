package com.cesde.aguas_paraiso.model.embeddable;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ubicacion {

    private String nombrePredio;

    private String vereda;

    private String corregimiento;

    private String municipio;

    private String observaciones;
}