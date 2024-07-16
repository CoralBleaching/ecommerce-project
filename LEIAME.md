[View in English](./README.md)

# Projeto de Ecommerce

Esta é uma solução completa para um site de ecommerce. É construído dentro do framework MVC, com backend em Java e PostgreSQL. A aplicação no lado do servidor também apresenta um backoffice implementado em HTML puro. O frontend é construído com React.js.

Criado para o projeto final do curso de Programação Web (2023.1, Universidade Federal do Ceará)

### Nota: este é um trabalho em progresso.

## Rodando/visualizando este projeto

### Versão online

Há uma [versão online deste projeto hospedada no Github Pages](https://coralbleaching.github.io/ecommerce-project/). Esta versão é adaptada para funcionar no Github Pages e contém mais bugs e menos funcionalidades do que a aplicação real.

### Executando localmente

O único pré-requisito obrigatório é ter [Docker](https://www.docker.com) instalado.

Depois de obter o Docker, clone este repositório via CLI do Git `git clone https://github.com/CoralBleaching/ecommerce-project.git`, ou via [Github Desktop](https://desktop.github.com), ou ainda baixe e descompacte via o menu "Code/Código".

Então, abra um terminal na pasta raiz do repositório e execute este comando para construir e lançar os contêineres: `docker-compose up --build`. Uma vez que a construção e configuração terminarem:

  * o App estará disponível em [http://localhost:3000](http://localhost:3000)
  * o Servidor estará disponível em [http://localhost:8080](http://localhost:8080)
  * o Banco de Dados estará disponível em [http://localhost:5432](http://localhost:5432)

## Objetivos

Este projeto visa demonstrar várias habilidades, dentre as quais:

  * Gestão de dados:
    * Aquisição, Armazenamento, Manutenção, Recuperação
    * Utilização de tecnologias de banco de dados relacional (PostgreSQL)
  * Java e frameworks relacionados (Servlets, Maven, Tomcat)
  * JavaScript e frameworks relacionados (React.js, Node.js, TypeScript)
  * Código limpo, bons princípios, POO e SOLID
  * Capacidade de trabalhar tanto rigorosamente quanto criativamente como arquiteto de sistemas
  * Desenvolvimento geral de aplicativos/web
  * Controle de versão, colaboração (Git, Github/Lab)

## A FAZER (TODO) (não inclusivo):

  * Adicionar um "login de recrutador"
  * Exibir o nome do usuário após o login
  * Exibir a contagem de produtos no botão "Carrinho" se o carrinho não estiver vazio
  * Exibir a contagem de produtos no cartão do produto se o produto foi adicionado ao carrinho
  * Permitir que as avaliações sejam excluídas
  * Adicionar a capacidade para o administrador editar produtos em lote (por exemplo, categoria/subcategoria ou hotness)
  * Repadronizar todos os nomes de rota, servlet e de arquivos em geral
  * Implementar paginação na lista de produtos do administrador
  * Melhorar a mensagem de erro de login do usuário
  * Limpar as variáveis de sessão para a seleção de produtos ao registrar um novo produto
  * Melhorar a mensagem de erro de registro de produto: produto já existe
  * Implementar servlet para remover fotos

## Bugs conhecidos (não inclusivo)

  * A pesquisa não atualiza a paginação
  * Alterar categorias/subcategorias não atualiza a paginação
  * Limpar a pesquisa não restaura a lista de produtos
  * Barra de pesquisa e menu lateral não estão fixos no lugar
  * A barra do carrinho sobrepõe o conteúdo do lado direito
  * A paginação é permitida crescer infinitamente com cliques no botão ">"
  * A pesquisa não está produzindo resultados todas as vezes
  * A seleção de pedidos está quebrada
