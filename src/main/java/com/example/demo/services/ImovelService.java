package com.example.demo.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import com.example.demo.models.ImovelModel;
import com.example.demo.repositories.ImovelRepository;

@Service
public class ImovelService {
    private final ImovelRepository repository;

    public ImovelService(ImovelRepository repository) {
        this.repository = repository;
    }

    public List<ImovelModel> getAll() {
        List<ImovelModel> list = repository.findAll();
        return list;
    }

    public List<String> getFinalidades() {
        return repository.findDistinctFinalidades();
    }

    public Page<ImovelModel> search(
            Integer tipoImovelId,
            Integer bairroId,
            String finalidade,
            BigDecimal precoMin,
            BigDecimal precoMax,
            Pageable pageable) {
        Specification<ImovelModel> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (tipoImovelId != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoImovel").get("id"), tipoImovelId));
            }
            if (bairroId != null) {
                predicates.add(criteriaBuilder.equal(root.get("bairro").get("id"), bairroId));
            }
            if (finalidade != null && !finalidade.isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.upper(root.get("finalidade")),
                        finalidade.trim().toUpperCase(Locale.ROOT)));
            }
            if (precoMin != null || precoMax != null) {
                String normalizedFinalidade = finalidade == null ? "" : finalidade.toUpperCase(Locale.ROOT);
                boolean aluguel = normalizedFinalidade.contains("ALUG");
                boolean venda = normalizedFinalidade.contains("VEND");
                Predicate vendaPrice = priceRange(root.get("precoVenda"), precoMin, precoMax, criteriaBuilder);
                Predicate aluguelPrice = priceRange(root.get("precoAluguel"), precoMin, precoMax, criteriaBuilder);

                if (aluguel) {
                    predicates.add(aluguelPrice);
                } else if (venda) {
                    predicates.add(vendaPrice);
                } else {
                    predicates.add(criteriaBuilder.or(vendaPrice, aluguelPrice));
                }
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
        return repository.findAll(specification, pageable);
    }

    private Predicate priceRange(
            Path<BigDecimal> price,
            BigDecimal min,
            BigDecimal max,
            CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.isNotNull(price));
        if (min != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(price, min));
        }
        if (max != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(price, max));
        }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    public ImovelModel find(Integer id) {
        Optional<ImovelModel> model = repository.findById(id);
        return model.orElse(null);
    }

    public ImovelModel insert(ImovelModel model) {
        return repository.save(model);
    }

    public ImovelModel update(ImovelModel model) {
        try {
            if (find(model.getId()) != null) {
                return repository.save(model);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
