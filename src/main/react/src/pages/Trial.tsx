import React, {Component} from "react";
import Button from "../components/Button";
import './Trial.css';
import {Scale} from "./Scale";
import {RouteComponentProps} from "react-router";
import {server} from "../utils/server";

interface UrlParams {
    name: string;
}

interface Props extends RouteComponentProps<UrlParams> {
}

export class Trial extends Component<Props, { scale: Scale, trialName: string }> {
    componentDidMount() {
        const {name} = this.props.match.params;
        this.setState({trialName: name});
        fetch(server.trial(name))
            .then(result => result.json())
            .then(json => this.fetchScale(json.scale));
    }

    private fetchScale(name: string) {
        fetch(server.scale(name))
            .then(result => result.json())
            .then(json => this.setState({scale: json}))
    }

    render() {
        return (this.state ? this.buttons(this.state.scale) : <span/>)
    }

    private buttons(scale: Scale) {
        return scale ? <div className={"trial"}>{scale.description}<br/>
            <ul>{scale.intensities.map((intensity, index) =>
                <li key={index}><Button trial={this.state.trialName} intensity={intensity.number}
                                        label={intensity.label}/></li>
            )}</ul>
        </div> : <div/>
    }
}