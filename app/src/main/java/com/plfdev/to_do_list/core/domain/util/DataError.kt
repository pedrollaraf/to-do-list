package com.plfdev.to_do_list.core.domain.util

sealed interface DataError : Error {

    sealed interface NetworkError : DataError {
        val friendlyMessage: String
        val statusCode: Int?
        val messageError: String?

        data class REQUEST_TIMEOUT(
            val code: Int? = null,
            val message: String?= null,
        ) : NetworkError {
            override val friendlyMessage = "A requisição demorou muito tempo para responder."
            override val statusCode get() =  code
            override val messageError: String? get() = message
        }

        data class BAD_REQUEST (
            val code: Int? = null,
            val message: String?= null,
        ) : NetworkError {
            override val friendlyMessage = "Não suportado. Verifique as credenciais"
            override val statusCode get() =  code
            override val messageError: String? get() = message
        }

        data class NOT_FOUND(
            val code: Int? = null,
            val message: String? = null,
        ) : NetworkError {
            override val friendlyMessage = "Não encontrado"
            override val statusCode get() =  code
            override val messageError: String? get() = message
        }

        data class NO_INTERNET(
            val code: Int? = null,
            val message: String?= null,
        ) : NetworkError {
            override val friendlyMessage = "Sem conexão com a internet."
            override val statusCode get() =  code
            override val messageError: String? get() = message
        }

        data class SERVER_ERROR(
            val code: Int? = null,
            val message: String?= null,
        ) : NetworkError {
            override val friendlyMessage = "Erro no servidor. Tente novamente mais tarde."
            override val statusCode get() =  code
            override val messageError: String? get() = message
        }

        data class SERIALIZATION(
            val code: Int? = null,
            val message: String?= null,
        ) : NetworkError {
            override val friendlyMessage = "Erro ao processar os dados."
            override val statusCode get() =  code
            override val messageError: String? get() = message
        }

        data class UNKNOWN(
            val code: Int? = null,
            val message: String?= null,
        ) : NetworkError {
            override val friendlyMessage = "Erro desconhecido."
            override val statusCode get() =  code
            override val messageError: String? get() = message
        }
    }

    sealed interface LocalError : DataError {
        val message: String

        data object DISK_FULL : LocalError {
            override val message = "O armazenamento do dispositivo está cheio."
        }

        data object UNKNOWN : LocalError {
            override val message = "Erro local desconhecido."
        }

        data object DATABASE_ERROR : LocalError {
            override val message = "Erro no banco de dados."
        }

        data object GET_TASKS_ERROR : LocalError {
            override val message = "Erro ao buscar as tarefas."
        }

        data object GET_UN_SYNCED_ERROR : LocalError {
            override val message = "Erro ao buscar tarefas não sincronizadas."
        }

        data object UPDATE_ERROR : LocalError {
            override val message = "Erro ao atualizar os dados."
        }

        data object INSERT_ERROR : LocalError {
            override val message = "Erro ao inserir os dados."
        }

        data object DELETE_ERROR : LocalError {
            override val message = "Erro ao deletar os dados."
        }
    }
}