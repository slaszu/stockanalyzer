package pl.slaszu.stockanalyzer.infrastructure.recommendation

import org.springframework.stereotype.Service
import pl.slaszu.stockanalyzer.domain.alert.model.CloseAlertModel
import pl.slaszu.stockanalyzer.domain.recommendation.SaveRepository

@Service
class QdrantSaveRepository : SaveRepository {
    override fun save(closeAlert: CloseAlertModel) {
        TODO("Not yet implemented")
    }

}