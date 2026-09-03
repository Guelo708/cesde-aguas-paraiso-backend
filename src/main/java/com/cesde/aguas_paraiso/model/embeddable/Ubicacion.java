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
    private String direccion;
    private String barrio;
    private String ciudad;
}

