package com.uca.pncparcialfinalrestaurante.repository;

import com.uca.pncparcialfinalrestaurante.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
    List<Mesa> findBySucursalId(Long sucursalId);
}
