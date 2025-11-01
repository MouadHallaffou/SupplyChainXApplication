package com.supplychainx.service_approvisionnement.mapper;

import com.supplychainx.service_approvisionnement.dto.FournisseurResponseDTO;
import com.supplychainx.service_approvisionnement.dto.MatierePremiereRequestDTO;
import com.supplychainx.service_approvisionnement.dto.MatierePremiereResponseDTO;
import com.supplychainx.service_approvisionnement.model.MatierePremiere;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MatierePremiereMapper {
    MatierePremiere toEntity(MatierePremiereRequestDTO dto);

    @Mapping(target = "fournisseurs", expression = "java(mapFournisseurs(matierePremiere.getFournisseurs()))")
    MatierePremiereResponseDTO toResponseDTO(MatierePremiere matierePremiere);

    @Mapping(target = "matierePremiereId", ignore = true) // Ignorer la mise à jour de l'ID
    void updateEntityFromDTO(MatierePremiereRequestDTO dto, @MappingTarget MatierePremiere entity);

    default List<FournisseurResponseDTO> mapFournisseurs(List<com.supplychainx.service_approvisionnement.model.Fournisseur> fournisseurs) {
        if (fournisseurs == null) {
            return null;
        }
        return fournisseurs.stream()
                .map(fournisseur -> FournisseurResponseDTO.builder()
                        .name(fournisseur.getName())
                        .contactEmail(fournisseur.getContactEmail())
                        .phoneNumber(fournisseur.getPhoneNumber())
                        .address(fournisseur.getAddress())
                        .rating(fournisseur.getRating())
                        .leadTimeDays(fournisseur.getLeadTimeDays())
                        .isActive(fournisseur.getIsActive())
                        .build())
                .toList();
    }
}
