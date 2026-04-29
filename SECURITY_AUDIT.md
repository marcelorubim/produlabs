# Relatório de Auditoria de Segurança - ProduLab

## ⚠️ VULNERABILIDADES CRÍTICAS ENCONTRADAS

### 🔴 CRÍTICO 1: Credenciais Hardcoded em Arquivos de Configuração

**Localização:**
- `src/main/resources/application.properties` (linhas 8-9, 15-16)
- `docker-compose.yml` (linhas 11-12)

**Problema:**
- Senha do banco de dados: `tAZAd2mZu5UERGnw`
- Senha do email Gmail: `jerviikokikludhj`
- Senha root do MySQL: `7QCWhwvG7UxwLJZ6`
- Email do sistema: `produlab.sistema@gmail.com`

**Risco:** Qualquer pessoa com acesso ao repositório pode:
- Acessar o banco de dados de produção
- Enviar emails em nome do sistema
- Comprometer completamente a infraestrutura

**Solução:**
1. Mover todas as credenciais para variáveis de ambiente
2. Criar arquivo `application.properties.example` sem credenciais
3. Adicionar `application.properties` ao `.gitignore`
4. Usar secrets management (ex: Kubernetes Secrets, AWS Secrets Manager, etc.)

---

### 🔴 CRÍTICO 2: Hash de Senhas Inseguro (SHA-256 sem Salt)

**Localização:**
- `src/main/java/br/com/produlab/service/AuthenticationService.java` (linha 48-59)

**Problema:**
```java
public String encodePassword(String password) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    // Sem salt, sem iterações, vulnerável a rainbow tables
}
```

**Risco:**
- SHA-256 é rápido e pode ser quebrado com força bruta
- Sem salt, senhas idênticas geram hashes idênticos
- Vulnerável a ataques de rainbow tables
- Se o banco for comprometido, todas as senhas podem ser quebradas

**Solução:**
- Migrar para bcrypt, Argon2 ou PBKDF2 com salt único por senha
- Usar biblioteca como `org.mindrot:jbcrypt` ou `org.bouncycastle:bcrypt`

---

### 🔴 CRÍTICO 3: Logs Expõem Informações Sensíveis

**Localização:**
- `src/main/java/br/com/produlab/entity/User.java` (linhas 71-72)
- `src/main/java/br/com/produlab/resource/UserResource.java` (linha 44)
- `src/main/java/br/com/produlab/resource/LaboratoryResource.java` (linha 46)

**Problema:**
```java
System.out.println(email);
System.out.println(password);  // ❌ Senha em texto plano nos logs!
System.out.println(user);      // ❌ toString() pode expor senha
```

**Risco:**
- Senhas e emails aparecem em logs de produção
- Logs podem ser acessados por pessoas não autorizadas
- Violação de LGPD/GDPR

**Solução:**
- Remover todos os `System.out.println` com dados sensíveis
- Usar logger apropriado (SLF4J) apenas para informações não sensíveis
- Remover senha do método `toString()` da entidade User

---

### 🟡 MÉDIO 1: Endpoint de Reset de Senha Sem Validação Adequada

**Localização:**
- `src/main/java/br/com/produlab/resource/AuthenticationResource.java` (linhas 53-60)

**Problema:**
```java
@POST
@PermitAll
@Path("/resetCredendials")
public Response resetCredendials(UpdateCredentialsRequest updateCredentialsRequest) {
    // Qualquer pessoa pode resetar senha de qualquer usuário apenas sabendo o email
    userService.resetCredendials(updateCredentialsRequest.getEmail());
}
```

**Risco:**
- Permite que qualquer pessoa reset a senha de qualquer usuário
- Não há verificação de identidade ou token de confirmação
- Permite enumeração de emails válidos

**Solução:**
- Implementar token de confirmação enviado por email
- Adicionar rate limiting
- Implementar CAPTCHA para prevenir abuso

---

### 🟡 MÉDIO 2: Código de Debug/Teste em Produção

**Localização:**
- `src/main/java/br/com/produlab/resource/UserResource.java` (linhas 77-84)

**Problema:**
```java
@GET
@Path("/reset/{id}")
public Response reset(@PathParam("id") Long id){
    // Endpoint de teste com mensagem hardcoded
    userService.sentEmail(currentUser,"Teste","Testessssssssssss");
}
```

**Risco:**
- Endpoint de teste exposto em produção
- Pode ser usado para enviar emails de teste
- Não tem validação adequada

**Solução:**
- Remover ou proteger endpoint de teste
- Adicionar validação adequada se necessário

---

### 🟡 MÉDIO 3: Chaves Privadas JWT no Código

**Localização:**
- `src/main/java/br/com/produlab/util/JWTUtil.java` (linha 44)
- `src/main/java/br/com/produlab/util/TokenUtils.java` (linha 43)

**Problema:**
- Chave privada JWT (`/private.pem`) está no classpath
- Se comprometida, permite gerar tokens válidos para qualquer usuário

**Risco:**
- Se a chave privada for exposta, atacantes podem criar tokens JWT válidos
- Acesso total ao sistema

**Solução:**
- Mover chave privada para variável de ambiente ou secrets management
- Nunca commitar chaves privadas no repositório
- Rotacionar chaves regularmente

---

### 🟢 BAIXO 1: Método toString() Expõe Senha

**Localização:**
- `src/main/java/br/com/produlab/entity/User.java` (linhas 83-91)

**Problema:**
```java
@Override
public String toString() {
    return "User{" +
        // ...
        ", password='" + password + '\'' +  // ❌ Senha exposta
        // ...
}
```

**Risco:**
- Se o objeto User for serializado ou logado, a senha será exposta

**Solução:**
- Remover senha do método `toString()`
- Já existe `@JsonbTransient` na propriedade, mas não protege `toString()`

---

## 📋 CHECKLIST DE CORREÇÕES

### Prioridade ALTA (Fazer ANTES de tornar público):
- [ ] Remover todas as credenciais hardcoded
- [ ] Migrar hash de senhas para bcrypt/Argon2
- [ ] Remover logs que expõem senhas
- [ ] Remover senha do método `toString()`
- [ ] Adicionar `application.properties` ao `.gitignore`
- [ ] Criar `application.properties.example`

### Prioridade MÉDIA:
- [ ] Implementar validação adequada no reset de senha
- [ ] Remover endpoints de teste/debug
- [ ] Mover chaves privadas JWT para variáveis de ambiente

### Prioridade BAIXA:
- [ ] Implementar rate limiting
- [ ] Adicionar CAPTCHA em endpoints públicos
- [ ] Revisar todos os logs para garantir que não expõem dados sensíveis

---

## 🔒 BOAS PRÁTICAS ADICIONAIS

1. **Variáveis de Ambiente:**
   - Usar `%env.VAR_NAME%` no Quarkus para variáveis de ambiente
   - Documentar todas as variáveis necessárias no README

2. **Secrets Management:**
   - Considerar usar Kubernetes Secrets, AWS Secrets Manager, ou HashiCorp Vault
   - Nunca commitar secrets no código

3. **Validação de Entrada:**
   - Implementar validação adequada em todos os endpoints
   - Proteger contra SQL Injection (já está usando Panache, mas verificar)

4. **Rate Limiting:**
   - Implementar rate limiting em endpoints de autenticação
   - Prevenir brute force attacks

5. **Monitoramento:**
   - Implementar logging adequado (sem dados sensíveis)
   - Monitorar tentativas de login falhadas
   - Alertas para atividades suspeitas

---

## 📝 NOTAS

- O projeto usa Quarkus 1.2.0.Final (versão antiga, considerar atualizar)
- SQL Injection parece estar protegido pelo uso de Panache com parâmetros nomeados
- Autorização baseada em roles parece estar implementada corretamente
- JWT está sendo usado para autenticação (boa prática)

---

**Data da Auditoria:** $(date)
**Versão do Código:** 1.0.0-SNAPSHOT

