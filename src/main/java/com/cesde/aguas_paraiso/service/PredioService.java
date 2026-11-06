package com.cesde.aguas_paraiso.service;

import com.cesde.aguas_paraiso.dto.PredioRequestDTO;
import com.cesde.aguas_paraiso.dto.PredioResponseDTO;
import com.cesde.aguas_paraiso.dto.SectorResponseDTO;
import com.cesde.aguas_paraiso.dto.TipoPredioResponseDTO;
import com.cesde.aguas_paraiso.dto.UsuarioResumenDTO;
import com.cesde.aguas_paraiso.model.embeddable.Ubicacion;
import com.cesde.aguas_paraiso.model.entity.Predio;
import com.cesde.aguas_paraiso.model.entity.Sector;
import com.cesde.aguas_paraiso.model.entity.TipoPredio;
import com.cesde.aguas_paraiso.model.entity.Usuario;
import com.cesde.aguas_paraiso.repository.PredioRepository;
import com.cesde.aguas_paraiso.repository.SectorRepository;
import com.cesde.aguas_paraiso.repository.TipoPredioRepository;
import com.cesde.aguas_paraiso.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar la lógica de negocio
 * relacionada con los predios de Aguas Paraíso.
 *
 * Predio es una entidad central del sistema porque se relaciona con:
 *
 * - Usuario, que actúa como propietario.
 * - Sector, que representa la zona geográfica.
 * - TipoPredio, que determina la clasificación del inmueble.
 * - Ubicacion, que almacena los datos detallados de localización.
 *
 * PredioRequestDTO recibe únicamente los identificadores de las
 * relaciones y los campos planos de ubicación.
 *
 * PredioResponseDTO devuelve información comprensible mediante:
 *
 * - UsuarioResumenDTO.
 * - SectorResponseDTO.
 * - TipoPredioResponseDTO.
 * - Campos planos de Ubicacion.
 * - Fechas de auditoría heredadas de BaseEntity.
 *
 * Las conversiones se realizan dentro del servicio,
 * sin utilizar una clase Mapper independiente.
 */
@Service
@Transactional(readOnly = true)
public class PredioService {

    /**
     * Repositorio principal utilizado para consultar
     * y modificar los predios.
     */
    private final PredioRepository predioRepository;

    /**
     * Repositorio utilizado para buscar la entidad Usuario
     * correspondiente al propietarioId recibido en el DTO.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Repositorio utilizado para buscar la entidad Sector
     * correspondiente al sectorId recibido en el DTO.
     */
    private final SectorRepository sectorRepository;

    /**
     * Repositorio utilizado para buscar la entidad TipoPredio
     * correspondiente al tipoPredioId recibido en el DTO.
     */
    private final TipoPredioRepository tipoPredioRepository;

    /**
     * Constructor utilizado por Spring para inyectar
     * automáticamente los repositorios necesarios.
     *
     * @param predioRepository repositorio de predios
     * @param usuarioRepository repositorio de usuarios
     * @param sectorRepository repositorio de sectores
     * @param tipoPredioRepository repositorio de tipos de predio
     */
    public PredioService(
            PredioRepository predioRepository,
            UsuarioRepository usuarioRepository,
            SectorRepository sectorRepository,
            TipoPredioRepository tipoPredioRepository
    ) {
        this.predioRepository = predioRepository;
        this.usuarioRepository = usuarioRepository;
        this.sectorRepository = sectorRepository;
        this.tipoPredioRepository = tipoPredioRepository;
    }

    /**
     * Consulta todos los predios registrados.
     *
     * Cada entidad Predio se convierte en PredioResponseDTO
     * antes de salir de la capa de servicio.
     *
     * @return lista de predios convertidos a DTO
     */
    public List<PredioResponseDTO> listarTodos() {
        return predioRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Busca un predio mediante su identificador interno.
     *
     * @param id identificador del predio
     * @return Optional con el DTO o vacío si no existe
     */
    public Optional<PredioResponseDTO> buscarPorId(
            Long id
    ) {
        return predioRepository.findById(id)
                .map(this::convertirADTO);
    }

    /**
     * Busca un predio mediante su código único.
     *
     * @param codigoPredio código asignado al predio
     * @return Optional con el DTO o vacío si no existe
     */
    public Optional<PredioResponseDTO> buscarPorCodigo(
            String codigoPredio
    ) {
        return predioRepository
                .findByCodigoPredio(codigoPredio)
                .map(this::convertirADTO);
    }

    /**
     * Consulta todos los predios asociados a un propietario.
     *
     * @param usuarioId identificador del usuario propietario
     * @return lista de predios del propietario convertidos a DTO
     */
    public List<PredioResponseDTO> buscarPorPropietario(
            Long usuarioId
    ) {
        return predioRepository.findByPropietarioId(usuarioId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Consulta todos los predios asociados a un sector.
     *
     * @param sectorId identificador del sector
     * @return lista de predios del sector convertidos a DTO
     */
    public List<PredioResponseDTO> buscarPorSector(
            Long sectorId
    ) {
        return predioRepository.findBySectorId(sectorId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Consulta todos los predios correspondientes
     * a una clasificación de tipo de predio.
     *
     * @param tipoPredioId identificador del tipo de predio
     * @return lista de predios convertidos a DTO
     */
    public List<PredioResponseDTO> buscarPorTipoPredio(
            Long tipoPredioId
    ) {
        return predioRepository.findByTipoPredioId(tipoPredioId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    /**
     * Registra un nuevo predio.
     *
     * REGLAS DE NEGOCIO CONSERVADAS:
     *
     * 1. El código del predio debe ser único.
     * 2. El propietario debe existir.
     * 3. El sector debe existir.
     * 4. El tipo de predio debe existir.
     *
     * El cliente envía solamente los IDs de las relaciones.
     * El servicio recupera las entidades reales antes de guardar.
     *
     * @param dto datos necesarios para registrar el predio
     * @return predio creado como PredioResponseDTO
     */
    @Transactional
    public PredioResponseDTO registrar(
            PredioRequestDTO dto
    ) {
        validarDatosObligatorios(dto);

        if (predioRepository.existsByCodigoPredio(
                dto.codigoPredio()
        )) {
            throw new IllegalArgumentException(
                    "Ya existe un predio registrado con este código"
            );
        }

        Usuario propietario = buscarPropietario(
                dto.propietarioId()
        );

        Sector sector = buscarSector(
                dto.sectorId()
        );

        TipoPredio tipoPredio = buscarTipoPredio(
                dto.tipoPredioId()
        );

        Ubicacion ubicacion = construirUbicacion(dto);

        Predio predio = Predio.builder()
                .codigoPredio(dto.codigoPredio())
                .ubicacion(ubicacion)
                .propietario(propietario)
                .sector(sector)
                .tipoPredio(tipoPredio)
                .build();

        Predio predioGuardado = predioRepository.save(predio);

        return convertirADTO(predioGuardado);
    }

    /**
     * Actualiza un predio existente.
     *
     * Primero comprueba que el ID exista. Después valida que
     * el código solicitado no pertenezca a otro predio.
     *
     * También vuelve a buscar las entidades relacionadas mediante
     * los IDs recibidos y reconstruye el objeto Ubicacion.
     *
     * @param id identificador del predio que se actualizará
     * @param dto nuevos datos del predio
     * @return predio actualizado como PredioResponseDTO
     */
    @Transactional
    public PredioResponseDTO actualizar(
            Long id,
            PredioRequestDTO dto
    ) {
        validarDatosObligatorios(dto);

        Predio predioExistente = predioRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe un predio con el ID: " + id
                        )
                );

        Optional<Predio> predioConCodigo =
                predioRepository.findByCodigoPredio(
                        dto.codigoPredio()
                );

        if (predioConCodigo.isPresent()
                && !predioConCodigo.get()
                .getId()
                .equals(id)) {
            throw new IllegalArgumentException(
                    "El código del predio ya pertenece a otro registro"
            );
        }

        Usuario propietario = buscarPropietario(
                dto.propietarioId()
        );

        Sector sector = buscarSector(
                dto.sectorId()
        );

        TipoPredio tipoPredio = buscarTipoPredio(
                dto.tipoPredioId()
        );

        Ubicacion ubicacion = construirUbicacion(dto);

        predioExistente.setCodigoPredio(dto.codigoPredio());
        predioExistente.setUbicacion(ubicacion);
        predioExistente.setPropietario(propietario);
        predioExistente.setSector(sector);
        predioExistente.setTipoPredio(tipoPredio);

        Predio predioActualizado =
                predioRepository.save(predioExistente);

        return convertirADTO(predioActualizado);
    }

    /**
     * Elimina un predio mediante su identificador.
     *
     * Antes de eliminar, comprueba que el registro exista.
     *
     * @param id identificador del predio que se eliminará
     */
    @Transactional
    public void eliminar(Long id) {
        if (!predioRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "No existe un predio con el ID: " + id
            );
        }

        predioRepository.deleteById(id);
    }

    /**
     * Valida que el DTO y los campos indispensables estén presentes.
     *
     * Las relaciones se validan por sus identificadores porque
     * PredioRequestDTO no recibe entidades completas.
     *
     * @param dto datos de entrada del predio
     */
    private void validarDatosObligatorios(
            PredioRequestDTO dto
    ) {
        if (dto == null) {
            throw new IllegalArgumentException(
                    "Los datos del predio son obligatorios"
            );
        }

        if (dto.codigoPredio() == null
                || dto.codigoPredio().isBlank()) {
            throw new IllegalArgumentException(
                    "El código del predio es obligatorio"
            );
        }

        if (dto.propietarioId() == null) {
            throw new IllegalArgumentException(
                    "El predio debe tener un propietario"
            );
        }

        if (dto.sectorId() == null) {
            throw new IllegalArgumentException(
                    "El predio debe pertenecer a un sector"
            );
        }

        if (dto.tipoPredioId() == null) {
            throw new IllegalArgumentException(
                    "El predio debe tener un tipo de predio"
            );
        }
    }

    /**
     * Busca la entidad Usuario que actuará como propietario.
     *
     * @param propietarioId identificador recibido en el DTO
     * @return entidad Usuario existente
     */
    private Usuario buscarPropietario(
            Long propietarioId
    ) {
        return usuarioRepository.findById(propietarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El propietario indicado no existe"
                        )
                );
    }

    /**
     * Busca la entidad Sector relacionada con el predio.
     *
     * @param sectorId identificador recibido en el DTO
     * @return entidad Sector existente
     */
    private Sector buscarSector(
            Long sectorId
    ) {
        return sectorRepository.findById(sectorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El sector indicado no existe"
                        )
                );
    }

    /**
     * Busca la entidad TipoPredio relacionada con el predio.
     *
     * @param tipoPredioId identificador recibido en el DTO
     * @return entidad TipoPredio existente
     */
    private TipoPredio buscarTipoPredio(
            Long tipoPredioId
    ) {
        return tipoPredioRepository.findById(tipoPredioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El tipo de predio indicado no existe"
                        )
                );
    }

    /**
     * Construye el objeto embebido Ubicacion a partir de los
     * campos planos recibidos en PredioRequestDTO.
     *
     * @param dto datos de entrada del predio
     * @return objeto Ubicacion listo para incorporarse a Predio
     */
    private Ubicacion construirUbicacion(
            PredioRequestDTO dto
    ) {
        return Ubicacion.builder()
                .nombrePredio(dto.nombrePredio())
                .vereda(dto.vereda())
                .corregimiento(dto.corregimiento())
                .municipio(dto.municipio())
                .observaciones(dto.observaciones())
                .build();
    }

    /**
     * Convierte una entidad Predio en PredioResponseDTO.
     *
     * La respuesta presenta las relaciones mediante DTO y muestra
     * los datos de Ubicacion como campos planos.
     *
     * @param predio entidad que se desea convertir
     * @return predio preparado para la respuesta de la API
     */
    public PredioResponseDTO convertirADTO(
            Predio predio
    ) {
        Ubicacion ubicacion = predio.getUbicacion();

        return new PredioResponseDTO(
                predio.getId(),
                predio.getCodigoPredio(),
                convertirPropietarioAResumen(
                        predio.getPropietario()
                ),
                convertirSectorADTO(
                        predio.getSector()
                ),
                convertirTipoPredioADTO(
                        predio.getTipoPredio()
                ),
                ubicacion != null
                        ? ubicacion.getNombrePredio()
                        : null,
                ubicacion != null
                        ? ubicacion.getVereda()
                        : null,
                ubicacion != null
                        ? ubicacion.getCorregimiento()
                        : null,
                ubicacion != null
                        ? ubicacion.getMunicipio()
                        : null,
                ubicacion != null
                        ? ubicacion.getObservaciones()
                        : null,
                predio.getFechaCreacion(),
                predio.getFechaActualizacion()
        );
    }

    /**
     * Convierte el propietario en un DTO resumido.
     *
     * De esta forma, la respuesta identifica al propietario
     * mediante ID, cédula y nombre completo, sin exponer siempre
     * el celular y el correo electrónico.
     *
     * @param usuario entidad Usuario relacionada con el predio
     * @return información resumida del propietario
     */
    private UsuarioResumenDTO convertirPropietarioAResumen(
            Usuario usuario
    ) {
        if (usuario == null) {
            return null;
        }

        return new UsuarioResumenDTO(
                usuario.getId(),
                usuario.getCedula(),
                construirNombreCompleto(usuario)
        );
    }

    /**
     * Convierte la entidad Sector relacionada con el predio
     * en SectorResponseDTO.
     *
     * @param sector entidad Sector
     * @return DTO público del sector
     */
    private SectorResponseDTO convertirSectorADTO(
            Sector sector
    ) {
        if (sector == null) {
            return null;
        }

        return new SectorResponseDTO(
                sector.getId(),
                sector.getNombre(),
                sector.getDescripcion()
        );
    }

    /**
     * Convierte la entidad TipoPredio relacionada con el predio
     * en TipoPredioResponseDTO.
     *
     * @param tipoPredio entidad TipoPredio
     * @return DTO público del tipo de predio
     */
    private TipoPredioResponseDTO convertirTipoPredioADTO(
            TipoPredio tipoPredio
    ) {
        if (tipoPredio == null) {
            return null;
        }

        return new TipoPredioResponseDTO(
                tipoPredio.getId(),
                tipoPredio.getNombre(),
                tipoPredio.getDescripcion()
        );
    }

    /**
     * Construye el nombre completo del propietario sin agregar
     * espacios innecesarios cuando un segundo nombre o apellido
     * sea nulo o esté vacío.
     *
     * @param usuario entidad Usuario
     * @return nombre completo preparado para UsuarioResumenDTO
     */
    private String construirNombreCompleto(
            Usuario usuario
    ) {
        StringBuilder nombreCompleto = new StringBuilder();

        agregarParteNombre(
                nombreCompleto,
                usuario.getPrimerNombre()
        );
        agregarParteNombre(
                nombreCompleto,
                usuario.getSegundoNombre()
        );
        agregarParteNombre(
                nombreCompleto,
                usuario.getPrimerApellido()
        );
        agregarParteNombre(
                nombreCompleto,
                usuario.getSegundoApellido()
        );

        return nombreCompleto.toString();
    }

    /**
     * Añade una parte del nombre únicamente cuando contiene
     * información válida.
     *
     * @param nombreCompleto acumulador del nombre
     * @param parte nombre o apellido que se desea añadir
     */
    private void agregarParteNombre(
            StringBuilder nombreCompleto,
            String parte
    ) {
        if (parte != null && !parte.isBlank()) {
            if (!nombreCompleto.isEmpty()) {
                nombreCompleto.append(" ");
            }

            nombreCompleto.append(parte.trim());
        }
    }
}
