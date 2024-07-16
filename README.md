[Versão em português](./LEIAME.md)

# Ecommerce project

This is intended to be a complete solution for an ecommerce website. It's built within the MVC framework, with the backend in Java and PostgreSQL. The server side application also features a backoffice implemented in pure HTML. The frontend is built with React.js. 

Created for the final project of the Web Programming course (Spring 2023, Universidade Federal do Ceará)

### Note: this is a work in progress. 

## Running/viewing this project

### Live version 

There is a [live version of this project hosted on Github Pages](https://coralbleaching.github.io/ecommerce-project/). This version is adapted to work on Github Pages and contains more bugs and less functionality than the real application.

### Running locally

The only obligatory prerequisite is to have [Docker](https://www.docker.com/products/docker-desktop/) installed. 

Once you have Docker, clone this repository via the Git CLI `git clone https://github.com/CoralBleaching/ecommerce-project.git` or via [Github Desktop](https://desktop.github.com/download/) or download and unzip via the "Code" drop-down menu. 

Then open a terminal in the root folder of the repository and run this command to build and launch the containers: `docker-compose up --build`. Once the build and configuration finish:
- the App will be available at http://localhost:3000
- the Server will be available at http://localhost:8080
- the Database will be available at http://localhost:5432

## Goals

This project is supposed to demonstrate several skills, among them:
- Data management:
  - Acquisition, Storage, Maintenance, Retrieval
  - Utilization of Relational Database techonologies (PostgreSQL)
- Java and related frameworks (Servlets, Maven, Tomcat)
- JavaScript and related frameworks (React.js, Node.js, TypeScript)
- Clean code, good principles, OOP and SOLID
- Ability to work both rigorously and creatively as a systems architect
- General app/web development
- Version control, collaboration (Git, Github/Lab)

## TODO (not all inclusive):
- Add a "Recruiter's login"
- Display user's name after sign in
- Display product count in "Cart" button if cart is not empty
- Display product count in product card if product has been added to cart
- Allow for evaluations to be deleted
- Add ability for admin to edit products in batch (e.g. category/subcategory or hotness)
- Re-standardize all route, servlet and general filenames
- Implement admin product list pagination
- Improve user login error message
- Clear session variables for product selection when registering a new product
- Improve product registration error message: product already exists
- Implement servlet for removing pictures

## Known bugs (not all inclusive)
- Search does not refresh pagination
- Changing categories/subcategories does not refresh pagination
- Clearing search does not restore the products list
- Search bar and side menu are not fixed in place
- Cart bar hangs over rightside content
- Pagination is allowed to grow *ad infinitum* with clicks on ">" button
- Search is not producing results everytime
- Order selection is broken