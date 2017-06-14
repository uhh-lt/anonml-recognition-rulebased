package ml.anon.recognition.rulebased.api;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.java.Log;
import ml.anon.annotation.ReplacementGenerator;
import ml.anon.model.anonymization.Anonymization;
import ml.anon.model.anonymization.Label;
import ml.anon.model.docmgmt.Document;
import ml.anon.recognition.rulebased.api.model.AbstractRule;
import ml.anon.recognition.rulebased.api.model.LicencePlateRule;
import ml.anon.recognition.rulebased.api.model.Rule;
import ml.anon.recognition.rulebased.api.repository.RuleRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirco on 11.06.17.
 */

@Service
@Log
public class AnnotationService {

    @Resource
    private RuleRepository repo;

    private Multimap<Label, Rule> rules = ArrayListMultimap.create();

    @PostConstruct
    private void buildRules() {
        List<AbstractRule> regExpRule = repo.findAll();
        rules.put(Label.LICENCE_PLATE, new LicencePlateRule());
        regExpRule.forEach(rule -> rules.put(rule.getLabel(), rule));

    }

    public List<Anonymization> annotate(Document doc) {
        List<Anonymization> result = new ArrayList<>();
        rules.entries().forEach(a -> {
            log.info("Applying rule " + a.getValue());
            List<Anonymization> apply = a.getValue().apply(doc, new ReplacementGenerator());
            log.info("Found " + apply.size());
            result.addAll(apply);
        });
        return result;
    }


}