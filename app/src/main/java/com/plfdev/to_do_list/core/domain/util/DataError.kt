package com.plfdev.to_do_list.core.domain.util

sealed interface DataError : Error {

    sealed interface NetworkError : DataError {
        val friendlyMessage: String
        val statusCode: Int?

        data object REQUEST_TIMEOUT : NetworkError {
            override val friendlyMessage = "A requisição demorou muito tempo para responder."
            override val statusCode = 408
        }

        data object UNAUTHORIZED : NetworkError {
            override val friendlyMessage = "Não autorizado. Verifique suas credenciais."
            override val statusCode = 401
        }

        data object FORBIDDEN : NetworkError {
            override val friendlyMessage = "Não autorizado pelo servidor."
            override val statusCode = 403
        }

        data object NOT_FOUND : NetworkError {
            override val friendlyMessage = "Não encontrado"
            override val statusCode = 404
        }

        data object CONFLICT : NetworkError {
            override val friendlyMessage = "Conflito de dados encontrado."
            override val statusCode = 409
        }

        data object TOO_MANY_REQUESTS : NetworkError {
            override val friendlyMessage = "Muitas requisições em um curto intervalo de tempo."
            override val statusCode = 429
        }

        data object NO_INTERNET : NetworkError {
            override val friendlyMessage = "Sem conexão com a internet."
            override val statusCode = null
        }

        data object PAYLOAD_TOO_LARGE : NetworkError {
            override val friendlyMessage = "Payload muito grande."
            override val statusCode = 413
        }

        data object SERVER_ERROR : NetworkError {
            override val friendlyMessage = "Erro no servidor. Tente novamente mais tarde."
            override val statusCode = 500
        }

        data object SERIALIZATION : NetworkError {
            override val friendlyMessage = "Erro ao processar os dados."
            override val statusCode = null
        }

        data object UNKNOWN : NetworkError {
            override val friendlyMessage = "Erro desconhecido."
            override val statusCode = null
        }

        data object SYNC : NetworkError {
            override val friendlyMessage = "Erro ao sincronizar os dados."
            override val statusCode = null
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