# Trade It

Este fonte é o backend do projeto fullstack do GoDev.

## Execução da aplicação

 

Para executar a aplicação tenha uma instância de um banco de dados postgres na versão 14 em execução. Após isso renomeie os arquivos ".env.template" para ".env" e "flyway.template.conf", e configure os valores de acordo com seu ambiente.

Para fazer o build com o maven execute os seguintes comandos:

```
mvn flyway:migrate
```
para executar as migrações do banco de dados


```
mvn spring-boot:run
```
para executar a aplicação


acesse o endpoint /swagger-ui.html para ver a documentação da API